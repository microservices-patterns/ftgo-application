#! /bin/bash -e

kubectl apply -f <(cat deployment/kubernetes/stateful-services/*.yml)

./deployment/kubernetes/scripts/kubernetes-wait-for-ready-pods.sh ftgo-mysql-0 ftgo-kafka-0 ftgo-dynamodb-local-0 ftgo-zookeeper-0

kubectl apply -f <(cat deployment/kubernetes/cdc-services/*.yml)

kubectl apply -f <(cat */src/deployment/kubernetes/*.yml)
