#! /bin/bash -e

CONTRACT_DIRS="common-contracts ftgo-order-service-contracts ftgo-restaurant-order-service-contracts"

COMMAND=$*

if [[ -z "$COMMAND" ]] ; then
   COMMAND=install
fi

echo Using $COMMAND

for dir in $CONTRACT_DIRS ; do
 (cd $dir ; ./mvnw $COMMAND)
 done
