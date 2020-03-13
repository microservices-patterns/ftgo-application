#!/usr/bin/env bash

export AWS_ACCESS_KEY_ID=id_key AWS_SECRET_ACCESS_KEY=access_key
aws --region ignored --endpoint-url http://${DOCKER_HOST_IP:=localhost}:8000 dynamodb scan --table-name ftgo-order-history

