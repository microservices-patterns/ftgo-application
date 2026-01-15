ARG baseImageVersion
FROM eventuateio/eventuate-examples-docker-images-spring-example-base-image:$baseImageVersion
COPY build/libs/order-history-service-main-0.1.0-SNAPSHOT.jar service.jar
HEALTHCHECK --interval=3s --retries=30 CMD curl -f http://localhost:8080/actuator/health || exit 1
