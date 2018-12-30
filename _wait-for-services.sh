#! /bin/bash

path=$1
shift
ports=$*

echo $path
echo $ports

host=$DOCKER_HOST_IP

done=false


while [[ "$done" = false ]]; do
	for port in $ports; do
		curl --fail http://${host}:${port}$path >& /dev/null
		if [[ "$?" -eq "0" ]]; then
			done=true
		else
			done=false
			echo http://${host}:${port}/health
			break
		fi
	done
	if [[ "$done" = true ]]; then
		echo connected
		break;
  fi
	echo -n .
	sleep 1
done
