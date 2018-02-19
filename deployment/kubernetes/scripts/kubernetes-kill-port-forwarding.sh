#! /bin/bash

set -e

kill $(cat port-forward-*.pid)

rm port-forward-*.pid
