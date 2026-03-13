#! /bin/bash -e

cd "$(dirname "$0")/ftgo-end-to-end-tests"

./gradlew cleanTest test
