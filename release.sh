#!/bin/bash

read -p "OSSRH PW: " ossrhpw
read -p "GPG PW:" gpgpw

mvn -f super-csv-declarative/pom.xml versions:set versions:commit -DnewVersion=$1
mvn -f super-csv-declarative/pom.xml clean deploy -Prelease -Denv.ossrhpw=$ossrhpw -Denv.gpgpw=$gpgpw