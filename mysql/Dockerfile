ARG EVENTUATE_COMMON_VERSION
FROM eventuateio/eventuate-mysql8:$EVENTUATE_COMMON_VERSION
COPY compile-schema-per-service.sh /docker-entrypoint-initdb.d/4.compile-schema-per-service.sh

ADD https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/master/mysql/1.initialize-database.sql /docker-entrypoint-initdb.d/template1
ADD https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/master/mysql/2.initialize-database.sql /docker-entrypoint-initdb.d/template2
ADD https://raw.githubusercontent.com/eventuate-tram/eventuate-tram-sagas/master/mysql/tram-saga-schema.sql /docker-entrypoint-initdb.d/template3

RUN cat /docker-entrypoint-initdb.d/template? | sed /eventuate/d > /docker-entrypoint-initdb.d/template

RUN touch /docker-entrypoint-initdb.d/5.schema-per-service.sql
RUN chown mysql -R /docker-entrypoint-initdb.d
