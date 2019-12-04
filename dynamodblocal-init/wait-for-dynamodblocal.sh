#! /bin/sh

done=false

while [[ "$done" = false ]]; do
    curl -q ${AWS_DYNAMODB_ENDPOINT_URL?} >& /dev/null
    if [[ "$?" -eq "0" ]]; then
        done=true
    else
        done=false
        break
    fi
	if [[ "$done" = true ]]; then
		echo connected
		break;
  fi
	echo -n .
	sleep 1
done
