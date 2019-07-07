#! /bin/bash -e

for schema in ftgo_accounting_service ftgo_consumer_service ftgo_order_service ftgo_kitchen_service ftgo_restaurant_service ;
do
    echo "create database $schema;" >> /docker-entrypoint-initdb.d/5.schema-per-service.sql
    echo "GRANT ALL PRIVILEGES ON $schema.* TO 'mysqluser'@'%' WITH GRANT OPTION;" >> /docker-entrypoint-initdb.d/5.schema-per-service.sql

    echo "USE $schema;" >> /docker-entrypoint-initdb.d/5.schema-per-service.sql
    cat /docker-entrypoint-initdb.d/template >> /docker-entrypoint-initdb.d/5.schema-per-service.sql
done
