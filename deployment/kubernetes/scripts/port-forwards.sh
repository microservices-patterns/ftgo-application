#! /bin/bash -e


#declare -A MYMAP=( ['ftgo-appi-gateway']=8087  )

doforward() {
    service=$1
    port=$2
    remotePort=$3
    pod=$(kubectl get pods --selector=svc=$service  -o jsonpath='{.items[*].metadata.name}')
    echo $service $pod $port
    kubectl port-forward $pod ${port}:${remotePort} &
    echo $! > port-forward-${service}.pid
}


doforward 'ftgo-accounting-service' 8085 8080

doforward 'ftgo-consumer-service' 8081 8080
doforward 'ftgo-api-gateway' 8087 8080
doforward 'ftgo-order-service' 8082 8080
doforward 'ftgo-restaurant-service' 8084 8080
doforward 'ftgo-kitchen-service' 8083 8080

exit 0



