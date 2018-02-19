#! /bin/bash -e

kubectl delete -f <(cat */src/deployment/kubernetes/*.yml deployment/kubernetes/*/*.yml)
