#! /bin/bash -e

. ./set-env.sh

docker-compose down -v
docker-compose up -d --build dynamodblocal mysql

./wait-for-mysql.sh

echo mysql is started

echo preparing dynamodblocal table data
cd ftgo-order-history-service
./create-dynamodb-tables.sh
cd ..

echo data is prepared

docker-compose up -d --build eventuate-local-cdc-service tram-cdc-service

# TODO Temporarily

./build-contracts.sh

./gradlew -x :ftgo-end-to-end-tests:test $* build

docker-compose build

./gradlew integrationTest


SPRING_DATASOURCE_URL=jdbc:mysql://${DOCKER_HOST_IP?}/ftgoorderservice ./gradlew :ftgo-order-service:componentTest

docker-compose down -v
docker-compose up -d

echo preparing dynamodblocal table data
cd ftgo-order-history-service
./create-dynamodb-tables.sh
cd ..


date

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

docker-compose down -v
