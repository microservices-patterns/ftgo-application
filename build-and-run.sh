#! /bin/bash -e

. ./set-env.sh

./gradlew assemble

docker-compose build

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

initializeDynamoDB

docker-compose up -d

./show-swagger-ui-urls.sh
