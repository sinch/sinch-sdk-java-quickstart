#!/bin/sh

mvn -f pom-ci.xml clean spotless:apply

(cd templates/client && mvn clean package)
(cd tutorials && ./compile.sh)
(cd getting-started && ./compile.sh)

