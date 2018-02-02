#! /bin/bash -e

CONTRACT_DIRS="common-contracts ftgo-order-service-contracts ftgo-restaurant-order-service-contracts"

for dir in $CONTRACT_DIRS ; do
 (cd $dir ; ./mvnw install)
 done
