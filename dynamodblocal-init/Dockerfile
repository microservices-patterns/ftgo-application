FROM ubuntu:18.04
RUN apt-get update && apt-get install -y python && apt-get install -y wget && wget https://bootstrap.pypa.io/get-pip.py && python get-pip.py
RUN pip install awscli --upgrade
COPY create-dynamodb-tables.sh .
COPY ftgo-order-history.json .
COPY wait-for-dynamodblocal.sh .
CMD ./wait-for-dynamodblocal.sh && ./create-dynamodb-tables.sh
