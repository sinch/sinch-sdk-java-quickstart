#!/bin/sh

mvn -f pom-ci.xml clean spotless:apply

(cd templates/client && mvn clean package)

