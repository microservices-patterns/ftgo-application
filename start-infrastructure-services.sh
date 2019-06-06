#!/bin/bash -e

docker-compose up -d --build $* mysql cdc-service eventuate-local-cdc-service dynamodblocal

./initialize-dynamodb.sh


