#!/bin/bash

set -e -u

git checkout master
mvn clean javadoc:javadoc
git checkout gh-pages
rm -R apidocs

git add --all apidocs/
git commit -m"Updated generated documentation"
git push origin gh-pages

git checkout master
