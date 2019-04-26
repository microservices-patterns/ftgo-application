#! /bin/bash -e

set -o pipefail

SCRIPTS=" build-and-test-all.sh build-and-test-all-mariadb.sh build-and-test-all-schema-per-service.sh "

export LOGFILE=build/build-and-test-everything.log

mkdir -p build

date > ${LOGFILE}

for script in $SCRIPTS ; do
   echo '****************************************** Running' $script
   date >> ${LOGFILE}
   echo '****************************************** Running' $script >> ${LOGFILE}
   ./$script | tee -a ${LOGFILE}
done

echo 'Finished successfully!!!'
