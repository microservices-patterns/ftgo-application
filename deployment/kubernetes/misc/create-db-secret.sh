kubectl create secret generic ftgo-db-secret \
  --from-file=username=./dbuser.txt \
  --from-file=password=./dbpassword.txt