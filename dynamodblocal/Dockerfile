FROM amazon/dynamodb-local:1.18.0
ENTRYPOINT java -jar DynamoDBLocal.jar -inMemory -sharedDb -port 8000
HEALTHCHECK --start-period=5s --interval=5s CMD curl http://localhost:8000 || exit 1
