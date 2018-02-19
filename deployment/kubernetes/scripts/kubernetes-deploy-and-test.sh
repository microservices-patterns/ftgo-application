#!/usr/bin/env bash


set -e

./deployment/kubernetes/scripts/kubernetes-deploy-all.sh

./deployment/kubernetes/scripts/kubernetes-run-end-to-end-tests.sh

