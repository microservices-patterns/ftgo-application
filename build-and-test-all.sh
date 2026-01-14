#!/bin/bash -e

# Build and test all migrated services
# This script iterates over migrated service directories and runs their builds
#
# Usage: ./build-and-test-all.sh [gradle-args...]
# Example: ./build-and-test-all.sh --build-cache

# Capture any arguments to pass to gradlew
GRADLE_ARGS="$@"

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Clean local contract repository first
"$SCRIPT_DIR/clean-repo.sh"
echo ""

# Contract projects to publish before building services
CONTRACT_PROJECTS=(
  "ftgo-kitchen-service-contracts"
  "ftgo-accounting-service-contracts"
  "ftgo-consumer-service-contracts"
  "ftgo-restaurant-service-contracts"
  "ftgo-order-service-contracts"
)

# List of migrated services (add services as they are migrated)
MIGRATED_SERVICES=(
  "ftgo-order-service"
  "ftgo-consumer-service"
  "ftgo-kitchen-service"
  "ftgo-accounting-service"
  "ftgo-restaurant-service"
  "ftgo-delivery-service"
)

# Publish contract stubs first
echo "Publishing contract stubs..."
for project in "${CONTRACT_PROJECTS[@]}"; do
  echo "Publishing: $project"
  if [ -d "$project" ] && [ -f "$project/gradlew" ]; then
    (cd "$project" && ./gradlew publishStubsPublicationToLocalRepository $GRADLE_ARGS)
  else
    echo "WARNING: Contract project '$project' not found or missing gradlew"
  fi
done
echo "Contract stubs published."
echo ""

echo "Building and testing ${#MIGRATED_SERVICES[@]} migrated service(s)..."

for service in "${MIGRATED_SERVICES[@]}"; do
  echo ""
  echo "========================================"
  echo "Building: $service"
  echo "========================================"

  if [ ! -d "$service" ]; then
    echo "ERROR: Service directory '$service' does not exist"
    exit 1
  fi

  if [ ! -f "$service/gradlew" ]; then
    echo "ERROR: Service '$service' does not have a Gradle wrapper"
    exit 1
  fi

  (cd "$service" && ./gradlew build $GRADLE_ARGS)

  if [ $? -ne 0 ]; then
    echo "ERROR: Build failed for $service"
    exit 1
  fi

  echo "SUCCESS: $service build completed"
done

echo ""
echo "========================================"
echo "All ${#MIGRATED_SERVICES[@]} service(s) built successfully!"
echo "========================================"
