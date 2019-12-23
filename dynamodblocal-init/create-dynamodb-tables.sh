#! /bin/sh -e

echo Initializing DynamoDB at endpoint ${AWS_DYNAMODB_ENDPOINT_URL}

if aws dynamodb --region us-west-1 --endpoint-url ${AWS_DYNAMODB_ENDPOINT_URL?} describe-table --table-name ftgo-order-history ; then
    echo table exists
else

echo creating table
aws dynamodb $* create-table --region us-west-2 --endpoint-url ${AWS_DYNAMODB_ENDPOINT_URL?} --cli-input-json file://ftgo-order-history.json

fi

touch /tables-created

while [[ true ]] ; do
    echo sleeping...
    sleep 3600
done