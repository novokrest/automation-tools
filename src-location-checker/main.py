import argparse
import glob
import logging as log
import os
import subprocess as sp
from os import path

class SourcesEnv:
    def __init__(self, module_marker_filename, sources_dir, sub_sources_dirs):
        self.module_marker_filename = module_marker_filename
        self.sources_dir = sources_dir
        self.sub_sources_dirs = sub_sources_dirs

class Language:
    def __init__(self, name, file_ext, src_dir_name):
        self.name = name
        self.file_ext = file_ext
        self.src_dir_name = src_dir_name

class IncorrectLocation:
    def __init__(self, filepath, lang, module, src_dir, sub_sources_dir):
        self.filepath = filepath
        self.lang = lang
        self.module = module
        self.src_dir = src_dir
        self.sub_sources_dir = sub_sources_dir

    def __str__(self):
        return obj_to_str(self)

class MoveCommand:
    def __init__(self, src, target):
        self.src = src
        self.target = target

    def execute(self):
        log.info('Moving: src=%s, target=%s', self.src, self.target)
        os.makedirs(path.dirname(self.target), exist_ok=True)
        sp.run(f'mv {self.src} {self.target}', shell=True, check=True, capture_output=True)

    def __str__(self):
        return obj_to_str(self)

def obj_to_str(obj):
    return ', '.join(f'{attr}={value}' for attr, value in obj.__dict__.items())

JAVA_LANG = Language(
    name='java',
    file_ext=['.java'],
    src_dir_name='java'
)

SCALA_LANG = Language(
    name='scala',
    file_ext=['.scala'],
    src_dir_name='scala'
)

LANGUAGES = {
    lang.name: lang
    for lang in [
        JAVA_LANG,
        SCALA_LANG
    ]
}

POM_SOURCES_ENV = SourcesEnv(
    module_marker_filename='pom.xml',
    sources_dir='src',
    sub_sources_dirs=['main', 'test']
)

def main():
    init_log()
    args = parse_args()
    run(
        env=POM_SOURCES_ENV,
        root_dir=args.root_dir
    )

def init_log():
    log.basicConfig(level=log.INFO, format="%(asctime)s [%(threadName)-12.12s] [%(levelname)-5.5s]  %(message)s")

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '-d', 
        dest='root_dir',
        required=True,
        help='Root of directories with sources', 
        metavar='root_dir',    
    )
    return parser.parse_args()

def run(env, root_dir):
    incorrect_locations = find_incorrect_locations(env, root_dir)
    for lang, incorrect_files in incorrect_locations.items():
        log.info('%s\n%s\n', lang.upper(), '\n'.join(f'  {file}' for file in incorrect_files))
    move_commands = generate_move_commands(incorrect_locations)
    log.info('MOVE: total=%s\n%s\n', len(move_commands), '\n'.join([f'  {cmd.src}\n->{cmd.target}' for cmd in move_commands]))
    for cmd in move_commands:
        cmd.execute()

def find_incorrect_locations(env, root_dir):
    modules = find_modules(root_dir, env.module_marker_filename)
    src_modules = [module for module in modules if path.exists(path.join(module, env.sources_dir))]
    log.info('Modules stat: all=%s, src=%s', len(modules), len(src_modules))
    incorrect_locations = dict()
    for module in src_modules:
        log.info('Start analyzing module: module=%s', module)
        module_incorrect_locations = find_incorrect_locations_in_module(env, module)
        for lang, filepaths in module_incorrect_locations.items():
            if lang not in incorrect_locations:
                incorrect_locations[lang] = list()
            incorrect_locations[lang].extend(filepaths)    
    return incorrect_locations

def find_incorrect_locations_in_module(env, module):
    incorrect_files_by_lang = dict()
    for lang_name, lang in LANGUAGES.items():
        for sub_sources_dir in env.sub_sources_dirs:
            src_dir = path.join(module, env.sources_dir, sub_sources_dir, lang.src_dir_name)
            all_files = list_files(src_dir)
            correct_files = find_files_with_ext(src_dir, lang.file_ext)
            log.debug('Sources stat: module=%s, lang=%s, sub=%s, all=%s, correct=%s', module, lang_name, sub_sources_dir, len(all_files), len(correct_files))
            incorrect_files = [ 
                IncorrectLocation(
                    filepath=path.relpath(file, src_dir),
                    lang=lang_name,
                    module=module,
                    src_dir=env.sources_dir,
                    sub_sources_dir=sub_sources_dir
                ) for file in list(set(all_files) - set(correct_files))
            ]
            if len(incorrect_files) > 0:
                log.info('Found incorrect files: module=%s, language=%s, incorrect=%s', 
                    module, lang_name, len(incorrect_files))
                log.debug('Incorrect files: paths=%s', incorrect_files)
                if lang not in incorrect_files_by_lang:
                    incorrect_files_by_lang[lang_name] = list()
                incorrect_files_by_lang[lang_name].extend(incorrect_files)
                    
    return incorrect_files_by_lang

def find_files_with_ext(root_dir, file_ext_list):
    paths = glob.glob(*[f'{root_dir}/**/*{file_ext}' for file_ext in file_ext_list], recursive=True)
    return [p for p in paths if path.isfile(p)]

def list_files(root_dir):
    paths = glob.glob(f'{root_dir}/**/*', recursive=True)
    log.debug('File stat: dir=%s, paths=%s', root_dir, len(paths))
    return [p for p in paths if path.isfile(p)]

def find_modules(root_dir, module_marker_filename):
    files = glob.glob(f'{root_dir}/**/{module_marker_filename}', recursive=True)
    return [path.dirname(file) for file in files]

def generate_move_commands(incorrect_locations_by_lang):
    return list(filter(lambda x: x is not None, [
        generate_move_command(location)
        for incorrect_locations in incorrect_locations_by_lang.values()
        for location in incorrect_locations
    ]))

def generate_move_command(location):
    correct_lang_name = resolve_lang(location.filepath)
    if correct_lang_name is not None:
        return MoveCommand(
            src=path.join(location.module, location.src_dir, location.sub_sources_dir, LANGUAGES[location.lang].src_dir_name, location.filepath),
            target=path.join(location.module, location.src_dir, location.sub_sources_dir, LANGUAGES[correct_lang_name].src_dir_name, location.filepath)
        )

def resolve_lang(filepath):
    file_ext = path.splitext(filepath)[1]
    for lang in LANGUAGES.values():
        if file_ext in lang.file_ext:
            return lang.name
    log.warning('Failed to resolve lang: filename=%s', path.basename(filepath))

if __name__ == '__main__':
    main()
