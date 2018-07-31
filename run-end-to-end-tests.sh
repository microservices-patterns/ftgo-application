#! /bin/bash -e

. ./set-env.sh

KEEP_RUNNING=

while [ ! -z "$*" ] ; do
  case $1 in
    "--keep-running" )
      KEEP_RUNNING=yes
      ;;
    "--help" )
      echo ./run-end-to-end-tests.sh --keep-running --assemble-only
      ;;
  esac
  shift
done

initializeDynamoDB() {
    ./initialize-dynamodb.sh
}


./gradlew assemble

docker-compose up -d --build dynamodblocal mysql

./wait-for-mysql.sh

echo mysql is started

initializeDynamoDB

docker-compose up -d --build

date

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

./run-graphql-api-gateway-tests.sh

if [ -z "$KEEP_RUNNING" ] ; then
  docker-compose down -v
fi
