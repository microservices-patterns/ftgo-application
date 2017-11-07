#! /bin/bash -e

SNX=${1}
SN=${SNX?}


./gradlew :${SNX?}:assemble
docker-compose build ${SN?}
docker-compose up -d ${SN?}
docker-compose logs -f ${SN?}