#! /bin/bash -e

KEEP_RUNNING=
ASSEMBLE_ONLY=

if [ -z "$DOCKER_COMPOSE" ] ; then
    DOCKER_COMPOSE=docker-compose
fi

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

# TODO Temporarily

./build-contracts.sh

./gradlew testClasses

${DOCKER_COMPOSE?} down -v
${DOCKER_COMPOSE?} up -d --build dynamodblocal mysql

./gradlew waitForMySql

echo mysql is started

./gradlew initDynamoDb

${DOCKER_COMPOSE?} up -d --build eventuate-local-cdc-service tram-cdc-service

if [ -z "$ASSEMBLE_ONLY" ] ; then

  ./gradlew -x :ftgo-end-to-end-tests:test $* build

  ${DOCKER_COMPOSE?} build

  ./gradlew $* integrationTest

  # Component tests need to use the per-service database schema


  SPRING_DATASOURCE_URL=jdbc:mysql://${DOCKER_HOST_IP?}/ftgoorderservice ./gradlew :ftgo-order-service:cleanComponentTest :ftgo-order-service:componentTest

  # Reset the DB/messages

  ${DOCKER_COMPOSE?} down -v

  ${DOCKER_COMPOSE?} up -d dynamodblocal mysql

  ./gradlew waitForMySql

  echo mysql is started

  ./gradlew initDynamoDb

  ${DOCKER_COMPOSE?} up -d


else

  ./gradlew $* assemble

  ${DOCKER_COMPOSE?} up -d --build dynamodblocal mysql

  ./gradlew waitForMySql

  echo mysql is started

  ./gradlew initDynamoDb

  ${DOCKER_COMPOSE?} up -d --build

fi

./gradlew waitForServices --host="${DOCKER_HOST_IP}" --ports="8081 8082 8083 8084 8085 8086 8099 8098"

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

./run-graphql-api-gateway-tests.sh

if [ -z "$KEEP_RUNNING" ] ; then
  ${DOCKER_COMPOSE?} down -v
fi
