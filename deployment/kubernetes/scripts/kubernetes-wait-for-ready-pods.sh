#! /bin/bash

pods=$*

done=false

while [[ "$done" = false ]]; do
        for pod in $pods; do
            outcome=$(kubectl get pod $pod -o=jsonpath='{.status.containerStatuses[0].ready}')
            if [[ "$outcome" == "true" ]]; then
                    done=true
            else
                    done=false
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
