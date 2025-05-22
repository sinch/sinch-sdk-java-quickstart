#!/bin/sh

DIRECTORIES="
conversation/send-text-message/client
numbers/rent-first-available-number/client
numbers/search-available/client
sms/send-sms-message/client
sms/respond-to-incoming-message/server
verification/user-verification-using-sms-pin/client
voice/respond-to-incoming-call/server
voice/make-a-call/client
"

for DIRECTORY in $DIRECTORIES
do
 (cd "$DIRECTORY" && mvn clean package) || exit 1
done
