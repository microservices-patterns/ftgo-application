#! /bin/bash -e

aws dynamodb $* create-table --cli-input-json file://ftgo-order-history.json
