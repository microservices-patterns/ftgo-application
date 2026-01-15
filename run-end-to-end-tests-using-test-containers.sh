#!/bin/bash

set -e

echo "Running end-to-end tests using TestContainers..."
echo ""

cd ftgo-end-to-end-tests

./gradlew cleanEndToEndTest
./gradlew endToEndTest -PendToEndTestMode=TestContainers "$@"

echo ""
echo "End-to-end tests completed successfully!"
