#!/bin/sh

DIRECTORIES="
sms/respond-to-incoming-message/server
verification/user-verification-using-sms-pin/client
voice/respond-to-incoming-call/server
"

for DIRECTORY in $DIRECTORIES
do
 (cd "$DIRECTORY" && mvn clean package) || exit 1
done
