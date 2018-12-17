#! /bin/bash -e

./build-and-test-all.sh --keep-running --assemble-only

cat update-mysql.sql | ./mysql-cli.sh -i
docker-compose -f docker-compose.yml -f docker-compose-unified-cdc.yml up -d --no-deps eventuate-local-cdc-service tram-cdc-service
./run-end-to-end-tests.sh

docker-compose -f docker-compose.yml -f docker-compose-unified-cdc.yml down
