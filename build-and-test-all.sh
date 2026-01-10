#!/bin/bash -e

# Build and test all migrated services
# This script iterates over migrated service directories and runs their builds

# List of migrated services (add services as they are migrated)
MIGRATED_SERVICES=(
  "ftgo-order-service"
)

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

  (cd "$service" && ./gradlew build)

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
