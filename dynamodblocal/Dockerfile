FROM openjdk:7-jre

# Create working space
WORKDIR /var/dynamodb_wd

# Default port for DynamoDB Local
EXPOSE 8000

COPY dynamodb_local_latest.tar.gz /tmp

RUN tar xfz /tmp/dynamodb_local_latest.tar.gz

# Default command for image
CMD ["/usr/bin/java", "-Djava.library.path=.", "-jar", "DynamoDBLocal.jar", "-dbPath", "/var/dynamodb_local", "-sharedDb", "-port", "8000"]

# Add VOLUMEs to allow backup of config, logs and databases
VOLUME ["/var/dynamodb_local", "/var/dynamodb_wd"]
