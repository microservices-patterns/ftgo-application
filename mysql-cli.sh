#! /bin/bash -e

docker run $* \
   --name mysqlterm --rm --network=${PWD##*/}_default \
   mysql:5.7.13  \
   mysql -hmysql -P3306 -uroot -prootpassword
