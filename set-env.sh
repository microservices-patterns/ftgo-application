if [ -z "$DOCKER_HOST_IP" ] ; then
    if [ ! -z "$DOCKER_HOST" ] ; then
      echo using ${DOCKER_HOST?}
      XX=${DOCKER_HOST%\:*}
      export DOCKER_HOST_IP=${XX#tcp\:\/\/}
    fi
fi


if [ -z "$DOCKER_HOST_IP" ] ; then
  echo DOCKER_HOST_IP is not set - localhost will be used
else
  echo DOCKER_HOST_IP is ${DOCKER_HOST_IP}
fi

export COMPOSE_HTTP_TIMEOUT=240
