FROM python:3.11.5-alpine3.18
RUN pip install awscli --upgrade
COPY create-dynamodb-tables.sh .
COPY ftgo-order-history.json .
COPY wait-for-dynamodblocal.sh .
RUN chmod +x *.sh
HEALTHCHECK --interval=10s --retries=10 --timeout=3s CMD [[ -f /tables-created ]]

CMD ./wait-for-dynamodblocal.sh && ./create-dynamodb-tables.sh
