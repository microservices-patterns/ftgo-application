if [ -z "$DOCKER_HOST_IP" ] ; then
    if [ -z "$DOCKER_HOST" ] ; then
      export DOCKER_HOST_IP=`hostname`
    else
      echo using ${DOCKER_HOST?}
      XX=${DOCKER_HOST%\:*}
      export DOCKER_HOST_IP=${XX#tcp\:\/\/}
    fi
fi

echo DOCKER_HOST_IP is $DOCKER_HOST_IP
export EVENTUATE_CURRENT_TIME_IN_MILLISECONDS_SQL="ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000)"
export COMPOSE_HTTP_TIMEOUT=240