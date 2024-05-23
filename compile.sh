#!/bin/sh

mvn clean spotless:apply

(cd templates/client && mvn clean package)

