#! /bin/bash -e

aws dynamodb $* create-table --region us-west-2 --endpoint-url ${AWS_DYNAMODB_ENDPOINT_URL} --cli-input-json file://ftgo-order-history.json
