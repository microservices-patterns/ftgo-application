#!/usr/bin/env bash

set -e

./deployment/kubernetes/scripts/kubernetes-delete-all.sh
./deployment/kubernetes/scripts/kubernetes-delete-volumes.sh

./deployment/kubernetes/scripts/kubernetes-deploy-all.sh

