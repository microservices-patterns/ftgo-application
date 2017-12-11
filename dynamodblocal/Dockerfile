FROM openjdk:7-jre

# Default port for DynamoDB Local
EXPOSE 8000
RUN mkdir /var/dynamodb_local && (wget -q -O - https://s3-us-west-2.amazonaws.com/dynamodb-local/dynamodb_local_latest.tar.gz | tar -xzf - )

# Default command for image
CMD /usr/bin/java ${JAVA_OPTS} -Djava.library.path=. -jar DynamoDBLocal.jar -dbPath /var/dynamodb_local -sharedDb -port 8000
