#!/bin/bash

add_file() {
    f=$1
    echo $f > $f.txt
    git add $f.txt && git commit -m "REALTYBACK-9837 [realty-main] File: $f.txt."
}

rm -rf example
mkdir example
cd example
git init
echo r > README.md
git add README.md && git commit -m 'added readme'
git remote add origin 'git@github.com:novokrest/tmp.git'
git push origin master -f
git checkout -b feature/base

add_file 1
add_file 2
add_file 3
git push origin feature/base -f

curl \
    -XPOST \
    -H "Authorization: token $GIT_PR_TOKEN" \
    -d '{
            "title": "Test",
            "body": "Please pull these awesome changes in!",
            "head": "feature/base",
            "base": "master"
    }' \
    https://api.github.com/repos/novokrest/tmp/pulls
