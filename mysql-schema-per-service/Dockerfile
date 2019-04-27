FROM eventuateio/eventuate-tram-sagas-mysql:0.11.0.RELEASE

COPY common-schema.sql /docker-entrypoint-initdb.d/1.common-schema.sql
COPY compile-schema-per-service.sh /docker-entrypoint-initdb.d/2.compile-schema-per-service.sh
COPY eventuate-local-template /docker-entrypoint-initdb.d/eventuate-local-template
COPY eventuate-tram-template /docker-entrypoint-initdb.d/eventuate-tram-template


RUN touch /docker-entrypoint-initdb.d/3.schema-per-service.sql
RUN chown mysql -R /docker-entrypoint-initdb.d
