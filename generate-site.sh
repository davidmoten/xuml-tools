#!/bin/bash
PROJECT=xuml-tools
set -e
mvn clean install 
mvn site
mvn site:stage
cd ../davidmoten.github.io
git pull
mkdir -p $PROJECT
cp -r ../$PROJECT/target/staging/* $PROJECT/
git add .
git commit -am "update site reports for $PROJECT"
git push
