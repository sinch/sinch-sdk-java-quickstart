#!/bin/sh

(cd sms/user-consent-app    && mvn clean package)
(cd voice/capture-leads-app && mvn clean package)
