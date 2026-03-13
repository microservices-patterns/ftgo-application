#!/bin/bash

set -e

cd "$(dirname "$0")/ftgo-end-to-end-tests"

echo "Running end-to-end tests using TestContainers..."
echo ""

./gradlew cleanEndToEndTest
./gradlew endToEndTest -PendToEndTestMode=TestContainers "$@"

echo ""
echo "End-to-end tests completed successfully!"
