#! /bin/bash -e

. ./set-env.sh

docker-compose down -v
docker-compose up -d --build dynamodblocal
docker-compose up -d --build mysql

./wait-for-mysql.sh

echo mysql is started

echo preparing dynamodblocal table data
cd ftgo-order-history-service
./create-dynamodb-tables.sh
cd ..

echo data is prepared

docker-compose up -d --build eventuate-local-cdc-service tram-cdc-service

./gradlew -x :ftgo-end-to-end-tests:test build

docker-compose up -d --build

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

docker-compose down -v
