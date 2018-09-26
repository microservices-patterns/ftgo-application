#! /bin/bash -e

export FTGO_DOCKER_COMPOSE_FILES=docker-compose.yml,docker-compose-mariadb.yml

DOCKER_COMPOSE="docker-compose -f docker-compose.yml -f docker-compose-mariadb.yml" ./build-and-test-all.sh $*
