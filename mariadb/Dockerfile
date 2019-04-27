FROM mariadb:10.3.8
COPY replication.cnf /etc/mysql/conf.d
COPY *.sql /docker-entrypoint-initdb.d/
ADD https://raw.githubusercontent.com/eventuate-tram/eventuate-tram-sagas/0.11.0.RELEASE/mysql/tram-saga-schema.sql /docker-entrypoint-initdb.d/2.eventuate-tram-saga-schema.sql
RUN chmod +r /docker-entrypoint-initdb.d/2.eventuate-tram-saga-schema.sql
