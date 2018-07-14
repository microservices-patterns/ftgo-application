#!/bin/bash -e

if which npm ; then
   echo npm on path. attempting to test GraphQL API gateway
else
   echo No Node.JS detected. Skipping test of GraphQL API gateway
   exit 0
fi

cd ftgo-api-gateway-graphql

if [ ! -d node_modules ] ; then
  npm install
fi

npm run unit-test
npm run end-to-end-test

