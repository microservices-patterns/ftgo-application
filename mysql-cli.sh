#! /bin/bash -e

docker-compose version

docker network ls

docker run $* \
   --name mysqlterm --rm \
   mysql:5.7.13  \
   mysql -hhost.docker.internal -P3306 -uroot -prootpassword
