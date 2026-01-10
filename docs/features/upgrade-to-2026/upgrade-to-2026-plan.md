# FTGO Application Upgrade to 2026 - Steel Thread Implementation Plan

## Instructions for Coding Agent

Before starting any work on this plan:

1. **ALWAYS** use the `idea-to-code:plan-tracking` skill to track task completion in this plan file
2. **ALWAYS** write code using TDD:
   - Use the `idea-to-code:tdd` skill when implementing code
   - **NEVER** write production code (`src/main/java/**/*.java`) without first writing a failing test (`src/test/java/**/*.java`)
   - Before using the Write tool on any `.java` file in `src/main/`, ask: "Do I have a failing test for this?" If not, write the test first.
   - When task direction changes mid-implementation, return to TDD PLANNING state and write a test first
3. **ALWAYS** after completing a task, when tests pass and the task has been marked complete, commit the changes

**Reference Projects:** Use these as templates throughout the migration:
- `/Users/cer/src/eventuate-examples/eventuate-tram-examples-customers-and-orders` - Build configuration, contract tests
- `/Users/cer/src/eventuate-examples/eventuate-tram-sagas-examples-customers-and-orders` - Saga patterns
- `/Users/cer/src/eventuate-examples/eventuate-examples-realguardio` - Self-contained service structure

---

## Steel Thread 1: Project Infrastructure and Order Service Foundation

This steel thread establishes the new project structure and migrates the Order Service as the first self-contained service.

### Task 1.1: Create root-level orchestration infrastructure

- [x] Create `build-and-test-all.sh` script at project root
  - [x] Script iterates over migrated service directories
  - [x] Runs `./gradlew build` in each service directory
  - [x] Exits with error if any service build fails
  - [x] Initially contains only `ftgo-order-service`
- [x] Create initial `docker-compose.yaml` at project root
  - [x] Define `order-service-db` PostgreSQL container
  - [x] Define `kafka` container (Confluent Kafka, KRaft mode)
  - [x] Define `cdc-service` for Eventuate CDC
  - [x] Reference realguardio docker-compose.yaml for configuration patterns
- [x] Verify `build-and-test-all.sh` executes (will fail until Order Service is migrated)

### Task 1.2: Transform ftgo-order-service into self-contained Gradle project

The `ftgo-order-service/` directory already exists. Transform it into a self-contained project:

- [ ] Create `ftgo-order-service/settings.gradle` with:
  - [ ] `pluginManagement` block for Eventuate testing plugins
  - [ ] `rootProject.name = 'ftgo-order-service'`
- [ ] Create `ftgo-order-service/gradle.properties` with version properties from reference projects:
  - [ ] `eventuatePlatformVersion=2025.1.BUILD-SNAPSHOT`
  - [ ] `springBootVersion=3.4.0`
  - [ ] `springCloudVersion=2024.0.0`
  - [ ] `eventuateTestPluginsVersion` from reference project
  - [ ] `eventuateMavenRepoUrl`
- [ ] Rewrite `ftgo-order-service/build.gradle` to be self-contained (not dependent on root build):
  - [ ] Java 17 toolchain configuration
  - [ ] Spring Boot 3.4.0 plugin
  - [ ] Eventuate testing plugins (integration-tests, component-tests)
  - [ ] Platform BOMs for dependency management
  - [ ] Maven Central repository only (no JCenter)
  - [ ] All dependencies declared directly (not inherited from root)
- [ ] Copy Gradle wrapper from reference project to `ftgo-order-service/gradle/`
- [ ] Create `ftgo-order-service/gradlew` and `ftgo-order-service/gradlew.bat`
- [ ] Verify `./gradlew tasks` runs successfully in `ftgo-order-service/`

### Task 1.3: Embed API and shared classes into Order Service

Copy external dependencies into the service to make it self-contained:

- [ ] Copy classes from `ftgo-order-service-api/` into `ftgo-order-service/src/main/java/`
- [ ] Copy required classes from `ftgo-common/` (Money, Address, PersonName, etc.) into `ftgo-order-service/src/main/java/`
- [ ] Copy required classes from `ftgo-common-jpa/` into `ftgo-order-service/src/main/java/`
- [ ] Update package declarations if needed
- [ ] Remove compile dependencies on `ftgo-order-service-api`, `ftgo-common`, `ftgo-common-jpa` from build.gradle

### Task 1.4: Migrate Order Service source code to Spring Boot 3.x / Jakarta EE

Update existing source files for compatibility:

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files:
  - [ ] `javax.persistence.*` → `jakarta.persistence.*`
  - [ ] `javax.validation.*` → `jakarta.validation.*`
  - [ ] `javax.servlet.*` → `jakarta.servlet.*`
- [ ] Update deprecated Spring Boot 2.x APIs to Spring Boot 3.x equivalents
- [ ] Replace Swagger/SpringFox annotations with SpringDoc OpenAPI
- [ ] Update `application.properties` / `application.yml` for Spring Boot 3.x

### Task 1.5: Migrate Order Service unit tests

Unit tests already exist in `ftgo-order-service/src/test/java/`. Update them:

- [ ] Update tests for JUnit 5 Platform (remove JUnit 4 annotations if present)
- [ ] Update `javax.*` to `jakarta.*` in test files
- [ ] Update test dependencies in `build.gradle`
- [ ] Verify `./gradlew test` passes

### Task 1.6: Migrate Order Service integration tests to Testcontainers

Integration tests may exist in `ftgo-order-service/src/integrationTest/java/` or need to be created:

- [ ] Convert Docker Compose-based tests to Testcontainers:
  - [ ] Add Testcontainers dependencies to `build.gradle`
  - [ ] Create base test class with `@Testcontainers` annotation
  - [ ] Configure PostgreSQL testcontainer
  - [ ] Configure Kafka testcontainer using Eventuate modules
- [ ] Remove Docker Compose plugin dependencies
- [ ] Update `javax.*` to `jakarta.*` in integration test files
- [ ] Verify `./gradlew integrationTest` passes

### Task 1.7: Migrate Order Service component tests to Testcontainers

Component tests may exist in `ftgo-order-service/src/componentTest/java/`. Update them:

- [ ] Convert to Testcontainers-based component tests:
  - [ ] Use `eventuate-platform-testcontainer-support-service` for full stack
  - [ ] Configure service container with proper environment
- [ ] Update `javax.*` to `jakarta.*` in component test files
- [ ] Verify `./gradlew componentTest` passes

### Task 1.8: Embed contract tests in Order Service

- [ ] Create `ftgo-order-service/src/contractTest/` directory structure
- [ ] Copy contract definitions from `ftgo-order-service-contracts/src/main/resources/contracts/`
- [ ] Copy contract test base classes from `ftgo-order-service-contracts/`
- [ ] Configure Spring Cloud Contract 4.x plugin in `build.gradle`
- [ ] Configure stub publishing to `build/repos/contracts/`
- [ ] Update contract base classes for Spring Boot 3.x
- [ ] Verify `./gradlew contractTest` passes
- [ ] Verify stubs are published to `build/repos/contracts/`

### Task 1.9: Update Order Service Dockerfile and verify Docker build

The Dockerfile may already exist. Update it for Java 17:

- [ ] Update `ftgo-order-service/Dockerfile` to use Eventuate base image pattern with Java 17
- [ ] Add health check configuration if not present
- [ ] Update root `docker-compose.yaml` to include `ftgo-order-service`
- [ ] Verify Docker image builds: `docker build -t ftgo-order-service .`
- [ ] Verify service starts in Docker and health check passes

### Task 1.10: Create end-to-end-tests project foundation

- [ ] Create `end-to-end-tests/` directory as standalone Gradle project
- [ ] Create `end-to-end-tests/settings.gradle` with plugin management
- [ ] Create `end-to-end-tests/build.gradle` with:
  - [ ] JUnit 5, RestAssured, Testcontainers dependencies
  - [ ] Eventuate testcontainer support
  - [ ] Task to assemble dependent services
- [ ] Copy Gradle wrapper to `end-to-end-tests/`
- [ ] Create basic end-to-end test that verifies Order Service starts and responds to health check
- [ ] Verify `./gradlew endToEndTest` passes

### Task 1.11: Verify full Order Service build pipeline

- [ ] Run `./build-and-test-all.sh` and verify it completes successfully
- [ ] Verify all test types pass: unit, integration, component, contract
- [ ] Verify Docker Compose can start Order Service with dependencies
- [ ] Commit all changes for Steel Thread 1

---

## Steel Thread 2: Consumer Service Migration

Migrate the Consumer Service as the first saga participant that Order Service communicates with.

### Task 2.1: Transform ftgo-consumer-service into self-contained Gradle project

The `ftgo-consumer-service/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties (copy from Order Service)
- [ ] Rewrite `build.gradle` to be self-contained (copy patterns from Order Service)
- [ ] Copy Gradle wrapper from Order Service
- [ ] Verify `./gradlew tasks` runs successfully

### Task 2.2: Embed API and shared classes into Consumer Service

- [ ] Copy classes from `ftgo-consumer-service-api/` into `ftgo-consumer-service/src/main/java/`
- [ ] Copy Order Service API classes needed to receive commands into `ftgo-consumer-service/src/main/java/`
- [ ] Copy required shared classes (Money, PersonName, etc.) into `ftgo-consumer-service/src/main/java/`
- [ ] Remove compile dependencies on external API modules from build.gradle

### Task 2.3: Migrate Consumer Service source code to Spring Boot 3.x / Jakarta EE

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files
- [ ] Update for Spring Boot 3.x compatibility
- [ ] Replace Swagger annotations with SpringDoc OpenAPI

### Task 2.4: Migrate Consumer Service tests to Testcontainers

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Convert integration tests to Testcontainers
- [ ] Convert component tests to Testcontainers
- [ ] Verify all tests pass: `./gradlew test integrationTest componentTest`

### Task 2.5: Embed contract tests in Consumer Service

- [ ] Copy contracts from `ftgo-consumer-service-contracts/` to `src/contractTest/`
- [ ] Configure Spring Cloud Contract 4.x
- [ ] Configure stub publishing
- [ ] Verify `./gradlew contractTest` passes

### Task 2.6: Update Consumer Service Dockerfile and infrastructure

- [ ] Update `ftgo-consumer-service/Dockerfile` for Java 17
- [ ] Add `consumer-service-db` to root `docker-compose.yaml`
- [ ] Add `ftgo-consumer-service` to root `docker-compose.yaml`
- [ ] Update `build-and-test-all.sh` to include Consumer Service
- [ ] Verify Docker build and service startup

### Task 2.7: Add end-to-end test for Order + Consumer validation

- [ ] Add end-to-end test that creates an order and verifies consumer validation step
- [ ] Test should start both Order Service and Consumer Service containers
- [ ] Verify CreateOrderSaga reaches consumer validation step
- [ ] Verify `./gradlew endToEndTest` passes in `end-to-end-tests/`

### Task 2.8: Verify Consumer Service integration

- [ ] Run `./build-and-test-all.sh` and verify both services build
- [ ] Verify Docker Compose starts both services
- [ ] Commit all changes for Steel Thread 2

---

## Steel Thread 3: Kitchen Service Migration

Migrate the Kitchen Service for ticket creation in the CreateOrderSaga.

### Task 3.1: Transform ftgo-kitchen-service into self-contained Gradle project

The `ftgo-kitchen-service/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties
- [ ] Rewrite `build.gradle` to be self-contained
- [ ] Copy Gradle wrapper
- [ ] Verify `./gradlew tasks` runs successfully

### Task 3.2: Embed API and shared classes into Kitchen Service

- [ ] Copy classes from `ftgo-kitchen-service-api/` into `ftgo-kitchen-service/src/main/java/`
- [ ] Copy Order Service API classes needed to receive commands
- [ ] Copy Restaurant Service API classes needed (restaurant events)
- [ ] Copy required shared classes
- [ ] Remove compile dependencies on external API modules

### Task 3.3: Migrate Kitchen Service source code to Spring Boot 3.x / Jakarta EE

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files
- [ ] Update for Spring Boot 3.x compatibility

### Task 3.4: Migrate Kitchen Service tests to Testcontainers

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Convert integration tests to Testcontainers
- [ ] Convert component tests to Testcontainers
- [ ] Verify all tests pass

### Task 3.5: Embed contract tests in Kitchen Service

- [ ] Copy contracts from `ftgo-kitchen-service-contracts/` to `src/contractTest/`
- [ ] Configure Spring Cloud Contract 4.x
- [ ] Configure stub publishing
- [ ] Verify `./gradlew contractTest` passes

### Task 3.6: Update Kitchen Service Dockerfile and infrastructure

- [ ] Update `ftgo-kitchen-service/Dockerfile` for Java 17
- [ ] Add `kitchen-service-db` to root `docker-compose.yaml`
- [ ] Add `ftgo-kitchen-service` to root `docker-compose.yaml`
- [ ] Update `build-and-test-all.sh` to include Kitchen Service
- [ ] Verify Docker build and service startup

### Task 3.7: Add end-to-end test for Order + Consumer + Kitchen

- [ ] Add end-to-end test that verifies ticket creation step
- [ ] Test should start Order, Consumer, and Kitchen Service containers
- [ ] Verify CreateOrderSaga reaches ticket creation step
- [ ] Verify ticket is created in Kitchen Service
- [ ] Verify `./gradlew endToEndTest` passes

### Task 3.8: Verify Kitchen Service integration

- [ ] Run `./build-and-test-all.sh` and verify all three services build
- [ ] Verify Docker Compose starts all services
- [ ] Commit all changes for Steel Thread 3

---

## Steel Thread 4: Accounting Service Migration and Full Saga Validation

Migrate the Accounting Service to complete the CreateOrderSaga flow.

### Task 4.1: Transform ftgo-accounting-service into self-contained Gradle project

The `ftgo-accounting-service/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties
- [ ] Rewrite `build.gradle` to be self-contained
- [ ] Copy Gradle wrapper
- [ ] Verify `./gradlew tasks` runs successfully

### Task 4.2: Embed API and shared classes into Accounting Service

- [ ] Copy classes from `ftgo-accounting-service-api/` into `ftgo-accounting-service/src/main/java/`
- [ ] Copy Order Service API classes needed to receive commands
- [ ] Copy Consumer Service API classes needed (consumer events)
- [ ] Copy required shared classes
- [ ] Remove compile dependencies on external API modules

### Task 4.3: Migrate Accounting Service source code to Spring Boot 3.x / Jakarta EE

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files
- [ ] Update for Spring Boot 3.x compatibility

### Task 4.4: Migrate Accounting Service tests to Testcontainers

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Convert integration tests to Testcontainers
- [ ] Convert component tests to Testcontainers
- [ ] Verify all tests pass

### Task 4.5: Embed contract tests in Accounting Service

- [ ] Copy contracts from `ftgo-accounting-service-contracts/` to `src/contractTest/`
- [ ] Configure Spring Cloud Contract 4.x
- [ ] Configure stub publishing
- [ ] Verify `./gradlew contractTest` passes

### Task 4.6: Update Accounting Service Dockerfile and infrastructure

- [ ] Update `ftgo-accounting-service/Dockerfile` for Java 17
- [ ] Add `accounting-service-db` to root `docker-compose.yaml`
- [ ] Add `ftgo-accounting-service` to root `docker-compose.yaml`
- [ ] Update `build-and-test-all.sh` to include Accounting Service
- [ ] Verify Docker build and service startup

### Task 4.7: Add end-to-end test for complete CreateOrderSaga happy path

- [ ] Add end-to-end test that exercises the full CreateOrderSaga:
  - [ ] Create order request
  - [ ] Consumer validation succeeds
  - [ ] Ticket created
  - [ ] Payment authorized
  - [ ] Ticket confirmed
  - [ ] Order approved
- [ ] Test should start all four saga participant services
- [ ] Verify order reaches APPROVED state
- [ ] Verify `./gradlew endToEndTest` passes

### Task 4.8: Verify full saga integration

- [ ] Run `./build-and-test-all.sh` and verify all four services build
- [ ] Verify Docker Compose starts all services
- [ ] Verify end-to-end test demonstrates complete saga flow
- [ ] Commit all changes for Steel Thread 4

---

## Steel Thread 5: Restaurant Service Migration

Migrate the Restaurant Service which provides menu data for order creation.

### Task 5.1: Transform ftgo-restaurant-service into self-contained Gradle project

The `ftgo-restaurant-service/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties
- [ ] Rewrite `build.gradle` to be self-contained
- [ ] Copy Gradle wrapper
- [ ] Verify `./gradlew tasks` runs successfully

### Task 5.2: Embed API and shared classes into Restaurant Service

- [ ] Copy classes from `ftgo-restaurant-service-api/` into `ftgo-restaurant-service/src/main/java/`
- [ ] Copy required shared classes
- [ ] Remove compile dependencies on external API modules

### Task 5.3: Migrate Restaurant Service source code to Spring Boot 3.x / Jakarta EE

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files
- [ ] Update for Spring Boot 3.x compatibility

### Task 5.4: Migrate Restaurant Service tests to Testcontainers

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Convert integration tests to Testcontainers
- [ ] Convert component tests to Testcontainers
- [ ] Verify all tests pass

### Task 5.5: Embed contract tests in Restaurant Service (if applicable)

- [ ] Check for existing contracts
- [ ] If contracts exist, copy to `src/contractTest/` and configure Spring Cloud Contract 4.x
- [ ] Verify contract tests pass

### Task 5.6: Update Restaurant Service Dockerfile and infrastructure

- [ ] Update `ftgo-restaurant-service/Dockerfile` for Java 17
- [ ] Add `restaurant-service-db` to root `docker-compose.yaml`
- [ ] Add `ftgo-restaurant-service` to root `docker-compose.yaml`
- [ ] Update `build-and-test-all.sh` to include Restaurant Service
- [ ] Verify Docker build and service startup

### Task 5.7: Add end-to-end test for restaurant data integration

- [ ] Add end-to-end test that creates a restaurant and verifies order can use restaurant data
- [ ] Verify restaurant events are published and consumed by Kitchen Service
- [ ] Verify `./gradlew endToEndTest` passes

### Task 5.8: Verify Restaurant Service integration

- [ ] Run `./build-and-test-all.sh` and verify all five services build
- [ ] Commit all changes for Steel Thread 5

---

## Steel Thread 6: Delivery Service Migration

Migrate the Delivery Service for delivery scheduling.

### Task 6.1: Transform ftgo-delivery-service into self-contained Gradle project

The `ftgo-delivery-service/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties
- [ ] Rewrite `build.gradle` to be self-contained
- [ ] Copy Gradle wrapper
- [ ] Verify `./gradlew tasks` runs successfully

### Task 6.2: Embed API and shared classes into Delivery Service

- [ ] Copy classes from `ftgo-delivery-service-api/` (if exists) into `ftgo-delivery-service/src/main/java/`
- [ ] Copy Order Service API classes needed for order events
- [ ] Copy Restaurant Service API classes needed
- [ ] Copy required shared classes
- [ ] Remove compile dependencies on external API modules

### Task 6.3: Migrate Delivery Service source code to Spring Boot 3.x / Jakarta EE

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files
- [ ] Update for Spring Boot 3.x compatibility

### Task 6.4: Migrate Delivery Service tests to Testcontainers

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Convert integration tests to Testcontainers
- [ ] Convert component tests to Testcontainers
- [ ] Verify all tests pass

### Task 6.5: Update Delivery Service Dockerfile and infrastructure

- [ ] Update `ftgo-delivery-service/Dockerfile` for Java 17
- [ ] Add `delivery-service-db` to root `docker-compose.yaml`
- [ ] Add `ftgo-delivery-service` to root `docker-compose.yaml`
- [ ] Update `build-and-test-all.sh` to include Delivery Service
- [ ] Verify Docker build and service startup

### Task 6.6: Add end-to-end test for delivery scheduling

- [ ] Add end-to-end test that verifies delivery is scheduled for approved order
- [ ] Verify `./gradlew endToEndTest` passes

### Task 6.7: Verify Delivery Service integration

- [ ] Run `./build-and-test-all.sh` and verify all six services build
- [ ] Commit all changes for Steel Thread 6

---

## Steel Thread 7: Order History Service Migration (CQRS)

Migrate the Order History Service which maintains a DynamoDB read model.

### Task 7.1: Transform ftgo-order-history-service into self-contained Gradle project

The `ftgo-order-history-service/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties
- [ ] Rewrite `build.gradle` to be self-contained
- [ ] Add AWS DynamoDB dependencies compatible with Java 17
- [ ] Copy Gradle wrapper
- [ ] Verify `./gradlew tasks` runs successfully

### Task 7.2: Embed API and shared classes into Order History Service

- [ ] Copy Order Service API classes needed for order events into `ftgo-order-history-service/src/main/java/`
- [ ] Copy required shared classes
- [ ] Remove compile dependencies on external API modules

### Task 7.3: Migrate Order History Service source code to Spring Boot 3.x / Jakarta EE

- [ ] Perform `javax.*` to `jakarta.*` migration on all source files
- [ ] Update AWS SDK dependencies for Java 17 compatibility
- [ ] Update for Spring Boot 3.x compatibility

### Task 7.4: Migrate Order History Service tests to Testcontainers

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Configure DynamoDB Local testcontainer
- [ ] Convert integration tests to Testcontainers
- [ ] Convert component tests to Testcontainers
- [ ] Verify all tests pass

### Task 7.5: Update Order History Service Dockerfile and infrastructure

- [ ] Update `ftgo-order-history-service/Dockerfile` for Java 17
- [ ] Add `dynamodb-local` to root `docker-compose.yaml`
- [ ] Add `ftgo-order-history-service` to root `docker-compose.yaml`
- [ ] Update `build-and-test-all.sh` to include Order History Service
- [ ] Verify Docker build and service startup

### Task 7.6: Add end-to-end test for CQRS query

- [ ] Add end-to-end test that creates an order and verifies it appears in Order History
- [ ] Verify order history query returns correct data
- [ ] Verify `./gradlew endToEndTest` passes

### Task 7.7: Verify Order History Service integration

- [ ] Run `./build-and-test-all.sh` and verify all seven services build
- [ ] Commit all changes for Steel Thread 7

---

## Steel Thread 8: API Gateway Migration

Migrate the API Gateway as the final service.

### Task 8.1: Transform ftgo-api-gateway into self-contained Gradle project

The `ftgo-api-gateway/` directory already exists. Transform it:

- [ ] Create `settings.gradle` with pluginManagement block
- [ ] Create `gradle.properties` with version properties
- [ ] Rewrite `build.gradle` to be self-contained
- [ ] Add Spring Cloud Gateway dependencies for Spring Boot 3.x
- [ ] Copy Gradle wrapper
- [ ] Verify `./gradlew tasks` runs successfully

### Task 8.2: Migrate API Gateway source code to Spring Boot 3.x / Jakarta EE

- [ ] Update Spring Cloud Gateway configuration for Spring Boot 3.x
- [ ] Perform `javax.*` to `jakarta.*` migration (if applicable)
- [ ] Update route configurations for new service structure

### Task 8.3: Migrate API Gateway tests

- [ ] Update unit tests for JUnit 5 and `javax.*` to `jakarta.*`
- [ ] Update any integration tests
- [ ] Verify all tests pass

### Task 8.4: Update API Gateway Dockerfile and infrastructure

- [ ] Update `ftgo-api-gateway/Dockerfile` for Java 17
- [ ] Add `ftgo-api-gateway` to root `docker-compose.yaml`
- [ ] Configure routing to all backend services
- [ ] Update `build-and-test-all.sh` to include API Gateway
- [ ] Verify Docker build and service startup

### Task 8.5: Add end-to-end test for full system via gateway

- [ ] Add end-to-end test that exercises CreateOrderSaga through API Gateway
- [ ] Verify all routes work correctly
- [ ] Verify `./gradlew endToEndTest` passes

### Task 8.6: Verify API Gateway integration

- [ ] Run `./build-and-test-all.sh` and verify all eight services build
- [ ] Commit all changes for Steel Thread 8

---

## Steel Thread 9: Final Cleanup and Validation

Remove legacy modules and validate the complete system.

### Task 9.1: Remove legacy API modules

- [ ] Delete `ftgo-order-service-api/` directory
- [ ] Delete `ftgo-consumer-service-api/` directory
- [ ] Delete `ftgo-kitchen-service-api/` directory
- [ ] Delete `ftgo-accounting-service-api/` directory
- [ ] Delete `ftgo-restaurant-service-api/` directory

### Task 9.2: Remove legacy contract modules

- [ ] Delete `ftgo-order-service-contracts/` directory
- [ ] Delete `ftgo-consumer-service-contracts/` directory
- [ ] Delete `ftgo-kitchen-service-contracts/` directory
- [ ] Delete `ftgo-accounting-service-contracts/` directory

### Task 9.3: Remove legacy API spec modules

- [ ] Delete `ftgo-consumer-service-api-spec/` directory
- [ ] Delete `ftgo-accounting-service-api-spec/` directory
- [ ] Delete `ftgo-restaurant-service-api-spec/` directory

### Task 9.4: Remove legacy shared modules

- [ ] Delete `ftgo-common/` directory
- [ ] Delete `ftgo-common-jpa/` directory
- [ ] Delete `common-swagger/` directory
- [ ] Delete `ftgo-test-util/` directory

### Task 9.5: Remove legacy build infrastructure

- [ ] Delete root-level `settings.gradle`
- [ ] Delete root-level `build.gradle`
- [ ] Delete `buildSrc/` directory
- [ ] Verify no root-level Gradle files remain

### Task 9.6: Final system validation

- [ ] Run `./build-and-test-all.sh` and verify all services build and test
- [ ] Run `docker-compose up` and verify all services start
- [ ] Run full end-to-end test suite
- [ ] Verify complete CreateOrderSaga flow:
  - [ ] Order created via API Gateway
  - [ ] Consumer validated
  - [ ] Ticket created
  - [ ] Payment authorized
  - [ ] Order approved
  - [ ] Order appears in Order History
- [ ] Commit final cleanup changes

### Task 9.7: Update documentation

- [ ] Update root README.md with new build instructions
- [ ] Document the self-contained service structure
- [ ] Document how to build and run individual services
- [ ] Document how to run end-to-end tests

---

## Summary

| Steel Thread | Services | Key Validation |
|--------------|----------|----------------|
| 1 | Order Service | First self-contained service builds and tests |
| 2 | + Consumer Service | Order + Consumer validation works |
| 3 | + Kitchen Service | Ticket creation works |
| 4 | + Accounting Service | Full CreateOrderSaga happy path |
| 5 | + Restaurant Service | Restaurant data integration |
| 6 | + Delivery Service | Delivery scheduling |
| 7 | + Order History Service | CQRS query works |
| 8 | + API Gateway | Full system via gateway |
| 9 | Cleanup | Legacy modules removed, final validation |
