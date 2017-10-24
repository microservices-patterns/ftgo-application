#! /bin/bash -e


docker-compose down -v
docker-compose up -d --build mysql

./wait-for-mysql.sh

docker-compose up -d --build eventuatelocalcdcservice tramcdcservice

./gradlew -x :ftgo-end-to-end-tests:test build

docker-compose up -d --build

./wait-for-services.sh

./gradlew :ftgo-end-to-end-tests:cleanTest :ftgo-end-to-end-tests:test

docker-compose down -v
