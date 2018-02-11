#! /bin/bash -e

DOCKER_COMPOSE_PREFIX=$(echo ${PWD##*/} | sed -e 's/-//g')_

DOCKER_REPO=msapatterns

$PREFIX docker login -u ${DOCKER_USER_ID?} -p ${DOCKER_PASSWORD?}

IMAGES="ftgo-consumer-service ftgo-order-service ftgo-restaurant-order-service ftgo-restaurant-service ftgo-accounting-service ftgo-order-history-service ftgo-api-gateway dynamodblocal-init"

cd dynamodblocal-init
./build-docker.sh
docker tag test-dynamodblocal-init:latest ${DOCKER_COMPOSE_PREFIX?}dynamodblocal-init
cd ..

function tagAndPush() {
  LOCAL=$1
  REMOTE="$2"
  FULL_LOCAL=${DOCKER_COMPOSE_PREFIX?}$LOCAL
  FULL_REMOTE="$DOCKER_REPO/$REMOTE:latest"

  $PREFIX docker tag $FULL_LOCAL $FULL_REMOTE
  $PREFIX docker push $FULL_REMOTE
}

for image in $IMAGES ; do
    tagAndPush $image $image
done
