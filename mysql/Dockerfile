ARG EVENTUATE_COMMON_VERSION
FROM eventuateio/eventuate-mysql8:$EVENTUATE_COMMON_VERSION
ARG EVENTUATE_COMMON_VERSION
ARG EVENTUATE_SAGA_VERSION

COPY compile-schema-per-service.sh /docker-entrypoint-initdb.d/4.compile-schema-per-service.sh

ADD https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/$EVENTUATE_COMMON_VERSION/mysql/1.initialize-database.sql /docker-entrypoint-initdb.d/template1
ADD https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/$EVENTUATE_COMMON_VERSION/mysql/2.initialize-database.sql /docker-entrypoint-initdb.d/template2
ADD https://raw.githubusercontent.com/eventuate-tram/eventuate-tram-sagas/$EVENTUATE_SAGA_VERSION/mysql/tram-saga-schema.sql /docker-entrypoint-initdb.d/template3

RUN cat /docker-entrypoint-initdb.d/template? | sed -e 's/eventuate.offset_store/offset_store/' -e /eventuate/d > /docker-entrypoint-initdb.d/template

RUN touch /docker-entrypoint-initdb.d/5.schema-per-service.sql
RUN chown mysql -R /docker-entrypoint-initdb.d
