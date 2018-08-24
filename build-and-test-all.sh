#! /bin/bash -e

KEEP_RUNNING=
ASSEMBLE_ONLY=

while [ ! -z "$*" ] ; do
  case $1 in
    "--keep-running" )
      KEEP_RUNNING=yes
      ;;
    "--assemble-only" )
      ASSEMBLE_ONLY=yes
      ;;
    "--help" )
      echo ./build-and-test-all.sh --keep-running --assemble-only
      exit 0
      ;;
  esac
  shift
done

echo KEEP_RUNNING=$KEEP_RUNNING

. ./set-env.sh

initializeDynamoDB() {
./initialize-dynamodb.sh
}

# TODO Temporarily

./build-contracts.sh

./gradlew testClasses

docker-compose down -v
docker-compose up -d --build dynamodblocal mysql

./wait-for-mysql.sh

echo mysql is started

initializeDynamoDB

docker-compose up -d --build eventuate-local-cdc-service tram-cdc-service


if [ -z "$ASSEMBLE_ONLY" ] ; then

  ./gradlew -x :ftgo-end-to-end-tests:test $* build

  docker-compose build

  ./gradlew $* integrationTest

  # Component tests need to use the per-service database schema


  SPRING_DATASOURCE_URL=jdbc:mysql://${DOCKER_HOST_IP?}/ftgoorderservice ./gradlew :ftgo-order-service:cleanComponentTest :ftgo-order-service:componentTest

  # Reset the DB/messages

  docker-compose down -v

  docker-compose up -d dynamodblocal mysql

  ./wait-for-mysql.sh

  echo mysql is started

  initializeDynamoDB

  docker-compose up -d


else

  ./gradlew $* assemble

  docker-compose up -d --build dynamodblocal mysql

  ./wait-for-mysql.sh

  echo mysql is started

  initializeDynamoDB

  docker-compose up -d --build

fi

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

./run-graphql-api-gateway-tests.sh

if [ -z "$KEEP_RUNNING" ] ; then
  docker-compose down -v
fi
