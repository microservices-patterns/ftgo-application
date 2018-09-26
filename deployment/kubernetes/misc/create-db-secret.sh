kubectl create secret generic ftgo-db-secret \
  --from-literal=username=mysqluser \
  --from-literal=password=mysqlpw