FROM eventuateio/eventuate-tram-sagas-mysql:0.10.0.M1

COPY common-schema.sql /docker-entrypoint-initdb.d/1.common-schema.sql
COPY compile-schema-per-service.sh /docker-entrypoint-initdb.d/2.compile-schema-per-service.sh
COPY eventuate-local-template /docker-entrypoint-initdb.d/eventuate-local-template
COPY eventuate-tram-template /docker-entrypoint-initdb.d/eventuate-tram-template


RUN touch /docker-entrypoint-initdb.d/3.schema-per-table.sql
RUN chown mysql -R /docker-entrypoint-initdb.d
