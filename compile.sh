#!/bin/sh

mvn -f pom-ci.xml clean spotless:apply || exit 1

(cd templates/client && mvn clean package) || exit 1
(cd templates/server && mvn clean package) || exit 1
(cd tutorials && ./compile.sh) || exit 1
(cd getting-started && ./compile.sh) || exit 1

