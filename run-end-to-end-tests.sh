#! /bin/bash -e

. ./set-env.sh

initializeDynamoDB() {
    echo preparing dynamodblocal table data
    cd dynamodblocal-init
    ./create-dynamodb-tables.sh
    cd ..
    echo data is prepared
}


./gradlew assemble

docker-compose up -d --build

./wait-for-mysql.sh

echo mysql is started

initializeDynamoDB

date

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

docker-compose down -v

