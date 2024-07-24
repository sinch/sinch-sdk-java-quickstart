#!/bin/sh

(cd sms/auto-subscribe-app && mvn clean package)
(cd voice/capture-leads-app && mvn clean package)
