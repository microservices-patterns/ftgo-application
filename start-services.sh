#! /bin/bash -e

docker-compose up -d mysql

./wait-for-mysql.sh

docker-compose up -d

echo -n waiting for the services to start...

./wait-for-services.sh
