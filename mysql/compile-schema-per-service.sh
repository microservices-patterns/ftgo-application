#! /bin/bash -e

for schema in ftgo_accounting_service ftgo_consumer_service ftgo_order_service ftgo_kitchen_service ftgo_restaurant_service ftgo_delivery_service;
do
  user=${schema}_user
  password=${schema}_password
  cat >> /docker-entrypoint-initdb.d/5.schema-per-service.sql <<END
  CREATE USER '${user}'@'%' IDENTIFIED BY '$password';
  create database $schema;
  GRANT ALL PRIVILEGES ON $schema.* TO '${user}'@'%' WITH GRANT OPTION;
  USE $schema;
END
    cat /docker-entrypoint-initdb.d/template >> /docker-entrypoint-initdb.d/5.schema-per-service.sql
done
