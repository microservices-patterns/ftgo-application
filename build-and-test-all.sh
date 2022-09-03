#! /bin/bash -e

KEEP_RUNNING=
ASSEMBLE_ONLY=
USE_EXISTING_CONTAINERS=

DATABASE_SERVICES="dynamodblocal mysql dynamodblocal-init"

while [ ! -z "$*" ] ; do
  case $1 in
    "--keep-running" )
      KEEP_RUNNING=yes
      ;;
    "--assemble-only" )
      ASSEMBLE_ONLY=yes
      ;;
    "--use-existing-containers" )
      USE_EXISTING_CONTAINERS=yes
      ;;
    "--help" )
      echo ./build-and-test-all.sh --keep-running --assemble-only --use-existing-containers
      exit 0
      ;;
  esac
  shift
done

echo KEEP_RUNNING=$KEEP_RUNNING

# TODO Temporarily

./gradlew --parallel buildContracts

./gradlew --parallel compileAll

if [ -z "$USE_EXISTING_CONTAINERS" ] ; then
    ./gradlew :composeDown
fi

./gradlew infrastructureComposeUp

echo mysql is started

# Test ./mysql-cli.sh

echo 'show databases;' | ./mysql-cli.sh -i

if [ -z "$ASSEMBLE_ONLY" ] ; then

  ./gradlew -x :ftgo-end-to-end-tests:test $* build

  ./gradlew $* integrationTest

  ./gradlew infrastructureComposeDown
  ./gradlew infrastructureComposeUp

  ./gradlew cleanComponentTest 

  # ./gradlew :ftgo-delivery-service:componentTest
  # ./gradlew :ftgo-order-service:componentTest
  
  ./gradlew componentTest

  ./gradlew :composeDown

else
  ./gradlew $* assemble

fi

./gradlew :composeUp

./run-end-to-end-tests.sh


# NEED TO FIX
# ./run-graphql-api-gateway-tests.sh

if [ -z "$KEEP_RUNNING" ] ; then
  ./gradlew :composeDown
fi
