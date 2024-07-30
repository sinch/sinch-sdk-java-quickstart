#!/bin/sh

(cd verification/user-verification-using-sms-pin/client && mvn clean package) || exit 1
(cd sms/respond-to-incoming-message/server && mvn clean package) || exit 1
