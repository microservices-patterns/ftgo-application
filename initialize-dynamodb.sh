#!/bin/bash -e

echo preparing dynamodblocal table data
cd dynamodblocal-init
./create-dynamodb-tables.sh
cd ..
echo data is prepared
