#! /bin/bash

./_wait-for-services.sh /actuator/health 8081 8082 8083 8084 8085 8086

./_wait-for-services.sh /health 8099 8098

