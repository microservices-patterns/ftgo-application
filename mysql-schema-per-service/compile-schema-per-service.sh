#! /bin/bash -e

eventuate_local_schemas=( ftgoaccountingservice )
eventuate_tram_schemas=( ftgoconsumerservice ftgoorderservice ftgokitchenservice ftgorestaurantservice ftgoaccountingservice ftgoorderhistoryservice )

for schema in "${eventuate_local_schemas[@]}"
do
    echo "USE $schema;" >> /docker-entrypoint-initdb.d/3.schema-per-table.sql
    cat /docker-entrypoint-initdb.d/eventuate-local-template >> /docker-entrypoint-initdb.d/3.schema-per-table.sql
done

for schema in "${eventuate_tram_schemas[@]}"
do
    echo "USE $schema;" >> /docker-entrypoint-initdb.d/3.schema-per-table.sql
    cat /docker-entrypoint-initdb.d/eventuate-tram-template >> /docker-entrypoint-initdb.d/3.schema-per-table.sql
done