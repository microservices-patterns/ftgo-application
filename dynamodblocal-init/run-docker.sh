#! /bin/bash -e

docker run -it --rm -e AWS_DYNAMODB_ENDPOINT_URL -e AWS_ACCESS_KEY_ID  -e AWS_SECRET_ACCESS_KEY test-dynamodblocal-init
