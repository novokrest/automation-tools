import argparse
import re
import os
import logging as log
import subprocess as sp
import requests as req
from enum import Enum
from git import Repo
from os import path


GIT_ACCESS_TOKEN_FILENAME = '~/.git-pr-generator'

REFACTORING_PR_BODY = '''# REFACTORING

### Что было сделано
{what_is_done}

### Затронутые компоненты
_{component_name}_

### Связанные PR
{parent_pr}
'''

CommitMsgRegex = re.compile(r'(\w+-\d+) \[([\w\-]+)\] (.+)')

Command = Enum('Command', 'SINGLE_COMMIT_PRS')

class CommitMsg:
    def __init__(self, task, component, msg):
        self.task = task
        self.component = component
        self.msg = msg

def main():
    init_log()
    args = parse_args()
    dispatch_command(args)

def init_log():
    log.basicConfig(level=log.INFO, format="%(asctime)s [%(threadName)-12.12s] [%(levelname)-5.5s]  %(message)s")

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '-d', 
        dest='git_dir',
        default='.',
        help='Git directory', 
        metavar='git_dir',    
    )
    parser.add_argument(
        '-c',
        dest='commits_count',
        type=int,
        default=-1,
    )
    parser.add_argument(
        '-u',
        dest='repo_owner',
        default='YandexClassifieds',
    )
    parser.add_argument(
        '-r',
        dest='repo_name',
        default='realty',
    )
    parser.add_argument(
        '--one-commit-prs',
        dest='cmd',
        action='store_const',
        const='SINGLE_COMMIT_PRS',
        default='SINGLE_COMMIT_PRS',
    )
    return parser.parse_args()

def dispatch_command(args):
    cmd = Command[args.cmd]
    if cmd == Command.SINGLE_COMMIT_PRS:
        generate_single_command_prs(
            git_dir=args.git_dir,
            commits_count=args.commits_count,
            repo_owner=args.repo_owner,
            repo_name=args.repo_name
        )
    else:
        log.warning('Unexpected command: cmd=%s', args.cmd)

def generate_single_command_prs(git_dir, commits_count, repo_owner, repo_name):
    cur_dir = os.getcwd()
    os.chdir(git_dir)
    try:
        log.info('Start generating PRs for each commit: git_dir=%s', git_dir)
        repo = Repo('.')
        commits = collect_all_commits(repo)[:commits_count]
        branch_name = repo.active_branch.name
        auth_token = read_auth_token()
        log.info('All commits were collected: branch_name=%s, count=%s', branch_name, len(commits))
        for commit, index in zip(commits, range(0, len(commits))):
            create_pr_by_commit(repo, branch_name, commit, index, auth_token, repo_owner, repo_name)
    finally:
        os.chdir(cur_dir)

def collect_all_commits(repo):
    master_branch = repo.branches.master
    master_commits = set(collect_commit_sha_list(repo, master_branch, 50))
    all_commits_since_first = []
    for commit in repo.iter_commits(rev=repo.head):
        if commit.hexsha not in master_commits:
            all_commits_since_first.append(commit)
        else:
            return all_commits_since_first[::-1]

def collect_commit_sha_list(repo, branch, max_depth):
    return [commit.hexsha for commit in repo.iter_commits(rev=branch, max_count=max_depth)]

def read_auth_token():
    with open(path.expanduser(GIT_ACCESS_TOKEN_FILENAME)) as f:
        return f.read().strip()

def create_pr_by_commit(repo, branch_name, commit, pr_number, auth_token, repo_owner, repo_name):
    new_branch_name = '{}-r{}'.format(branch_name, pr_number)
    new_branch = repo.create_head(new_branch_name, repo.refs.master)
    log.info('Branch was created from master: branch_name=%s', new_branch_name)
    new_branch.checkout()
    git_cherry_pick(commit.hexsha)
    create_pr_for_current_branch(repo, branch_name, commit.message, auth_token, repo_owner, repo_name)

def git_cherry_pick(commit_sha):
    log.info('Try cherry-pick: commit_sha=%s', commit_sha)
    completed_process = sp.run('git cherry-pick {}'.format(commit_sha), shell=True, check=True, capture_output=True)
    log.info(completed_process.stdout)

def create_pr_for_current_branch(repo, parent_branch_name, raw_commit_msg, auth_token, repo_owner, repo_name):
    branch_name = repo.active_branch.name
    commit_msg = parse_commit_msg(raw_commit_msg)
    component_name = commit_msg.component
    parent_pr_url = find_pr_url(parent_branch_name, auth_token, repo_owner, repo_name)
    log.info('Push branch: %s', branch_name)
    repo.remotes.origin.push(refspec=branch_name)
    log.info('Creating PR: branch_name=%s', branch_name)
    res = req.post(
        url='https://api.github.com/repos/{}/{}/pulls'.format(repo_owner, repo_name),
        headers={
            'Authorization': 'token {}'.format(auth_token)
        },
        json={
            'title': '{} {}'.format(commit_msg.task, commit_msg.msg),
            'body': REFACTORING_PR_BODY.format(
                what_is_done=commit_msg.msg, 
                component_name=component_name,
                parent_pr=parent_pr_url
            ),
            'head': branch_name,
            'base': 'master'
        }
    )
    log.info('PR was created: %s', res.json())

def find_pr_url(branch_name, auth_token, repo_owner, repo_name):
    res = req.get(
        url='https://api.github.com/repos/{}/{}/pulls'.format(repo_owner, repo_name),
        headers={
            'Authorization': 'token {}'.format(auth_token)
        }
    )
    json = res.json()
    for pr in json:
        if pr['head']['ref'] == branch_name:
            return pr['html_url']


def parse_commit_msg(commit_msg):
    log.info('Try parse commit message: %s', commit_msg)
    m = CommitMsgRegex.match(commit_msg)
    return CommitMsg(
        task=m.group(1),
        component=m.group(2),
        msg=m.group(3).strip()
    )

if __name__ == '__main__':
    main()
