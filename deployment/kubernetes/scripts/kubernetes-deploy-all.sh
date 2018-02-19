#! /bin/bash -e

for base in deployment/kubernetes/ftgo-kafka-config.yml deployment/kubernetes/ftgo-{kafka,zookeeper,mysql}-deployment.yml deployment/kubernetes/ftgo-dynamodb-local.yml deployment/kubernetes/ftgo-db-secret.yml ; do
    kubectl apply -f $base
done

./deployment/kubernetes/scripts/kubernetes-wait-for-ready-pods.sh ftgo-mysql-0 ftgo-kafka-0 ftgo-dynamodb-local-0 ftgo-zookeeper-0

kubectl apply -f deployment/kubernetes/ftgo-tram-cdc-service.yml
kubectl apply -f deployment/kubernetes/eventuate-local-cdc-service.yml

kubectl apply -f <(cat */src/deployment/kubernetes/*.yml)
