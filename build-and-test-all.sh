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

# Services that publish contract stubs (must be built first)
# Format: "service-dir:submodule-with-stubs"
STUB_PUBLISHERS=(
  "ftgo-order-service:order-service-event-publishing"
  "ftgo-consumer-service:consumer-service-event-publishing"
  "ftgo-kitchen-service:kitchen-service-event-publishing"
  "ftgo-accounting-service:accounting-service-command-handlers"
  "ftgo-restaurant-service:restaurant-service-event-publishing"
)

# List of migrated services (add services as they are migrated)
MIGRATED_SERVICES=(
  "ftgo-order-service"
  "ftgo-consumer-service"
  "ftgo-kitchen-service"
  "ftgo-accounting-service"
  "ftgo-restaurant-service"
  "ftgo-delivery-service"
  "ftgo-order-history-service"
)

# Publish contract stubs from migrated services first
echo "Publishing contract stubs from migrated services..."
for entry in "${STUB_PUBLISHERS[@]}"; do
  service="${entry%%:*}"
  submodule="${entry##*:}"
  echo "Publishing stubs: $service ($submodule)"
  if [ -d "$service" ] && [ -f "$service/gradlew" ]; then
    (cd "$service" && ./gradlew ":${submodule}:publishStubsPublicationToLocalRepository" $GRADLE_ARGS)
  else
    echo "WARNING: Service '$service' not found or missing gradlew"
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
