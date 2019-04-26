#! /bin/bash -e

export FTGO_DOCKER_COMPOSE_FILES=docker-compose-mysql-schema-per-service.yml
export ORDER_SERVICE_COMPONENT_TEST_EVENTUATE_DATABASE_SCHEMA=ftgoorderservice

DOCKER_COMPOSE="docker-compose -f docker-compose-mysql-schema-per-service.yml" ./build-and-test-all.sh $*
