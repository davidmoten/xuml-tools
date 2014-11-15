#!/bin/bash
PROJECT=xuml-tools
set -e
mvn clean install 
mvn site
cd ../davidmoten.github.io
git pull
mkdir -p $PROJECT
cp -r ../$PROJECT/target/site/* $PROJECT/
git add .
git commit -am "update site reports for $PROJECT"
git push
