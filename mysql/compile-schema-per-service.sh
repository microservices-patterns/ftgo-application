#! /bin/bash -e

eventuate_schemas=( ftgoaccountingservice ftgoconsumerservice ftgoorderservice ftgokitchenservice ftgorestaurantservice )

for schema in "${eventuate_schemas[@]}"
do
    echo "USE $schema;" >> /docker-entrypoint-initdb.d/5.schema-per-service.sql
    cat /docker-entrypoint-initdb.d/template >> /docker-entrypoint-initdb.d/5.schema-per-service.sql
done
