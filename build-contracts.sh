#! /bin/bash -e

CONTRACT_DIRS="ftgo-accounting-service-contracts ftgo-consumer-service-contracts ftgo-order-service-contracts ftgo-kitchen-service-contracts"

COMMAND=$*

if [[ -z "$COMMAND" ]] ; then
   COMMAND=publish
fi

echo Using $COMMAND

ARGS=

for dir in $CONTRACT_DIRS ; do
  ARGS="$ARGS :$dir:$COMMAND"
done

./gradlew $ARGS

