ARG EVENTUATE_COMMON_VERSION
FROM eventuateio/eventuate-vanilla-mysql8:$EVENTUATE_COMMON_VERSION
ARG EVENTUATE_COMMON_VERSION
ARG EVENTUATE_SAGA_VERSION

COPY compile-schema-per-service.sh /docker-entrypoint-initdb.d/4.compile-schema-per-service.sh

RUN touch /docker-entrypoint-initdb.d/5.schema-per-service.sql

RUN chown mysql -R /docker-entrypoint-initdb.d
