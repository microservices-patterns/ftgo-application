#! /bin/bash -e


for port in 8081 8084 8082 ; do
    open http://${DOCKER_HOST_IP?}:$port/swagger-ui.html
done
