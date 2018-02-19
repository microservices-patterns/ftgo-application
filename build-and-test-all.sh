#! /bin/bash -e

. ./set-env.sh

initializeDynamoDB() {
    echo preparing dynamodblocal table data
    cd dynamodblocal-init
    ./create-dynamodb-tables.sh
    cd ..
    echo data is prepared
}

docker-compose down -v
docker-compose up -d --build dynamodblocal mysql

./wait-for-mysql.sh

echo mysql is started

initializeDynamoDB

docker-compose up -d --build eventuate-local-cdc-service tram-cdc-service

# TODO Temporarily

./build-contracts.sh

./gradlew -x :ftgo-end-to-end-tests:test $* build

docker-compose build

./gradlew $* integrationTest


# Component tests need to use the per-service database schema

SPRING_DATASOURCE_URL=jdbc:mysql://${DOCKER_HOST_IP?}/ftgoorderservice ./gradlew :ftgo-order-service:cleanComponentTest :ftgo-order-service:componentTest

# Reset the DB/messages

docker-compose down -v
docker-compose up -d

initializeDynamoDB

date

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

docker-compose down -v
