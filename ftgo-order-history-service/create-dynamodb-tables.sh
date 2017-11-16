#! /bin/bash -e

aws dynamodb $* create-table --region us-west-2 --endpoint-url http://localhost:8000 --cli-input-json file://ftgo-order-history.json
