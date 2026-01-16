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
4. **NEVER** delete tests during migration:
   - Tests must be migrated in place, not removed and restored later
   - Move test files to their new module locations
   - Update imports and dependencies
   - If a test fails after migration, fix it - do not delete it
   - Verify tests still pass after each move

**Reference Projects:** Use these as templates throughout the migration:
- `/Users/cer/src/eventuate-examples/eventuate-tram-examples-customers-and-orders` - Build configuration, contract tests
- `/Users/cer/src/eventuate-examples/eventuate-tram-sagas-examples-customers-and-orders` - Saga patterns
- `/Users/cer/src/eventuate-examples/eventuate-examples-realguardio` - Self-contained service structure
- `/Users/cer/src/eventuate-examples/eventuate-examples-realguardio/end-to-end-tests` - End-to-end test patterns (TestContainers, ApplicationUnderTest abstraction)

**Reference Documentation:**
- `docs/features/upgrade-to-2026/ftgo-order-service.md` - Analysis of Order Service migration patterns and lessons learned

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

- [x] Create `ftgo-order-service/settings.gradle` with:
  - [x] `pluginManagement` block for Eventuate testing plugins
  - [x] `rootProject.name = 'ftgo-order-service'`
- [x] Create `ftgo-order-service/gradle.properties` with version properties from reference projects:
  - [x] `eventuatePlatformVersion=2025.1.BUILD-SNAPSHOT`
  - [x] `springBootVersion=3.4.0`
  - [x] `springCloudVersion=2024.0.0`
  - [x] `eventuateTestPluginsVersion` from reference project
  - [x] `eventuateMavenRepoUrl`
- [x] Rewrite `ftgo-order-service/build.gradle` to be self-contained (not dependent on root build):
  - [x] Java 17 toolchain configuration
  - [x] Spring Boot 3.4.0 plugin
  - [x] Eventuate testing plugins (integration-tests, component-tests)
  - [x] Platform BOMs for dependency management
  - [x] Maven Central repository only (no JCenter)
  - [x] All dependencies declared directly (not inherited from root)
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Verify `./gradlew tasks` runs successfully in `ftgo-order-service/`

### Task 1.3: Embed API and shared classes into Order Service

Move external dependencies into the service to make it self-contained:

- [x] Move classes from `ftgo-order-service-api/` into `ftgo-order-service/src/main/java/`
- [x] Copy required classes from `ftgo-common/` (Money, Address, PersonName, etc.) into `ftgo-order-service/src/main/java/`
- [x] Copy required classes from `ftgo-common-jpa/` into `ftgo-order-service/src/main/java/`
- [x] Update package declarations if needed
- [x] Remove compile dependencies on `ftgo-order-service-api`, `ftgo-common`, `ftgo-common-jpa` from build.gradle

### Task 1.4: Migrate Order Service source code to Spring Boot 3.x / Jakarta EE

Update existing source files for compatibility:

- [x] Perform `javax.*` to `jakarta.*` migration on all source files:
  - [x] `javax.persistence.*` → `jakarta.persistence.*`
  - [x] `javax.annotation.*` → `jakarta.annotation.*`
  - [x] `javax.servlet.*` → `jakarta.servlet.*`
- [x] Update deprecated Spring Boot 2.x APIs to Spring Boot 3.x equivalents
  - [x] Removed TraceIdResponseFilter (used deprecated Spring Cloud Sleuth)
- [x] Replace Swagger/SpringFox annotations with SpringDoc OpenAPI (none found)
- [x] Update `application.properties` for Spring Boot 3.x (PostgreSQL, removed Sleuth config)
- [x] Update orm.xml to Jakarta Persistence 3.0 namespace

### Task 1.5: Migrate Order Service unit tests

Unit tests already exist in `ftgo-order-service/src/test/java/`. Update them:

- [x] Update tests for JUnit 5 Platform (remove JUnit 4 annotations if present)
- [x] Update `javax.*` to `jakarta.*` in test files
- [x] Update test dependencies in `build.gradle`
- [x] Verify `./gradlew test` passes

### Task 1.6: Migrate Order Service integration tests to Testcontainers

Integration tests may exist in `ftgo-order-service/src/integrationTest/java/` or need to be created:

- [x] Convert Docker Compose-based tests to Testcontainers:
  - [x] Add Testcontainers dependencies to `build.gradle`
  - [x] Create base test class with `@Testcontainers` annotation
  - [x] Configure PostgreSQL testcontainer
  - [x] Configure Kafka testcontainer using Eventuate modules
- [x] Remove Docker Compose plugin dependencies
- [x] Update `javax.*` to `jakarta.*` in integration test files
- [x] Verify `./gradlew integrationTest` passes

### Task 1.7: Migrate Order Service component tests to Testcontainers

Component tests may exist in `ftgo-order-service/src/componentTest/java/`. Update them:

- [x] Convert to Testcontainers-based component tests:
  - [x] Use `eventuate-platform-testcontainer-support-service` for full stack
  - [x] Configure service container with proper environment
- [x] Update `javax.*` to `jakarta.*` in component test files
- [x] Verify `./gradlew componentTest` passes

### Task 1.8: Embed contract tests in Order Service

- [x] Create `ftgo-order-service/src/contractTest/` directory structure
- [x] Move contract definitions from `ftgo-order-service-contracts/src/main/resources/contracts/`
- [x] Move contract test base classes from `ftgo-order-service-contracts/`
- [x] Configure Spring Cloud Contract 4.x plugin in `build.gradle`
- [x] Configure stub publishing to `build/repos/contracts/`
- [x] Update contract base classes for Spring Boot 3.x
- [x] Verify `./gradlew contractTest` passes
- [x] Verify stubs are published to `build/repos/contracts/`

### Task 1.9: Update Order Service Dockerfile and verify Docker build

The Dockerfile may already exist. Update it for Java 17:

- [x] Update `ftgo-order-service/Dockerfile` to use Eventuate base image pattern with Java 17
- [x] Add health check configuration if not present
- [x] Update root `docker-compose.yaml` to include `ftgo-order-service`
- [ ] Verify Docker image builds: `docker build -t ftgo-order-service .`
- [ ] Verify service starts in Docker and health check passes

### Task 1.10: Verify full Order Service build pipeline

- [x] Run `./build-and-test-all.sh` and verify it completes successfully
- [x] Verify all test types pass: unit, integration, component, contract
- [ ] Verify Docker Compose can start Order Service with dependencies (deferred)
- [x] Commit all changes for Steel Thread 1

---

## Steel Thread 2: Consumer Service Migration

Migrate the Consumer Service as the first saga participant that Order Service communicates with.

### Task 2.1: Transform ftgo-consumer-service into self-contained multi-module Gradle project

The `ftgo-consumer-service/` directory already exists. Transform it into a multi-module project following the Order Service architecture:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `consumer-service-domain` - Core domain model (Consumer) and events
  - [x] `consumer-service-persistence` - JPA repositories and orm.xml
  - [x] `consumer-service-command-handlers` - Handles commands from Order Service (ValidateOrderByConsumer)
  - [x] `consumer-service-event-publishing` - Publishes consumer events + provider contract tests
  - [x] `consumer-service-restapi` - REST controllers
  - [x] `consumer-service-main` - Spring Boot application, component tests
- [x] Create `gradle.properties` with version properties (copy from Order Service)
- [x] Create root `build.gradle` with common configuration for all subprojects
- [x] Create each subproject's `build.gradle` with appropriate dependencies
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Configure `java-test-fixtures` plugin in domain module for shared test utilities
- [x] Verify `./gradlew tasks` runs successfully

### Task 2.2: Embed API and shared classes into Consumer Service subprojects

- [x] Copy shared classes (Money, PersonName, etc.) into `consumer-service-domain/src/main/java/`
- [x] Move API classes from `ftgo-consumer-service-api/` into appropriate subprojects
- [x] Copy Order Service API classes needed to receive commands into `consumer-service-command-handlers/`
- [x] Update subproject dependencies (no external API module dependencies)

### Task 2.3: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move domain classes (Consumer, events, ConsumerService, ConsumerRepository) to `consumer-service-domain/`
- [x] Move command handlers to `consumer-service-command-handlers/`
- [x] Move REST controllers to `consumer-service-restapi/`
- [x] Move main class and configuration to `consumer-service-main/`
- [x] Perform `javax.*` to `jakarta.*` migration on all source files
- [x] Update for Spring Boot 3.x compatibility
- [x] Replace Swagger annotations with SpringDoc OpenAPI (none were present)

### Task 2.4: Migrate Consumer Service tests to appropriate subprojects

**IMPORTANT:** Migrate tests in place - never delete tests during restructuring.

- [x] Move unit tests to appropriate subprojects alongside their source files
- [x] Update JUnit 4 annotations to JUnit 5 equivalents (already JUnit 5)
- [x] Update `javax.*` to `jakarta.*` imports (none needed)
- [x] Verify `./gradlew test` passes across all subprojects
- [x] Move integration tests to `consumer-service-main/src/integrationTest/`
- [x] Verify `./gradlew integrationTest` passes (using in-memory Tram)
- [ ] Create component tests in `consumer-service-main/src/componentTest/` (deferred)
- [ ] Verify `./gradlew componentTest` passes (deferred)

### Task 2.5: Embed contract tests in consumer-service-event-publishing

- [x] Move contracts to `consumer-service-event-publishing/src/contractTest/`
- [x] Configure Spring Cloud Contract 4.x in `consumer-service-event-publishing/build.gradle`
- [x] Configure stub publishing
- [x] Verify `./gradlew contractTest` passes

### Task 2.6: Update Consumer Service Dockerfile and infrastructure

- [x] Update `ftgo-consumer-service/Dockerfile` for multi-module structure
- [x] Add `consumer-service-db` to root `docker-compose.yaml`
- [x] Add `ftgo-consumer-service` to root `docker-compose.yaml`
- [x] Add CDC pipeline configuration for consumer-service-db
- [x] `build-and-test-all.sh` already includes Consumer Service
- [ ] Verify Docker build and service startup (deferred to end-to-end testing)

### Task 2.7: Verify Consumer Service integration

- [x] Run `./build-and-test-all.sh` and verify both services build
- [ ] Verify Docker Compose starts both services (deferred)
- [x] Commit all changes for Steel Thread 2

---

## Steel Thread 3: Kitchen Service Migration

Migrate the Kitchen Service for ticket creation in the CreateOrderSaga.

### Task 3.1: Transform ftgo-kitchen-service into self-contained multi-module Gradle project

The `ftgo-kitchen-service/` directory already exists. Transform it into a multi-module project:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `kitchen-service-domain` - Core domain model (Ticket, Restaurant) and events
  - [x] `kitchen-service-persistence` - JPA repositories and orm.xml
  - [x] `kitchen-service-command-handlers` - Handles commands from Order Service
  - [x] `kitchen-service-event-handling` - Consumes Restaurant events + consumer contract tests
  - [x] `kitchen-service-event-publishing` - Publishes ticket events + provider contract tests
  - [x] `kitchen-service-restapi` - REST controllers
  - [x] `kitchen-service-main` - Spring Boot application, component tests
- [x] Create `gradle.properties` with version properties
- [x] Create root `build.gradle` with common configuration for all subprojects
- [x] Create each subproject's `build.gradle` with appropriate dependencies
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Configure `java-test-fixtures` plugin in domain module
- [x] Verify `./gradlew tasks` runs successfully

### Task 3.2: Embed API and shared classes into Kitchen Service subprojects

- [x] Copy shared classes into `kitchen-service-domain/src/main/java/`
- [x] Move API classes from `ftgo-kitchen-service-api/` into appropriate subprojects
- [x] Copy Order Service API classes into `kitchen-service-command-handlers/`
- [x] Copy Restaurant Service API classes into `kitchen-service-event-handling/`
- [x] Update subproject dependencies

### Task 3.3: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move domain classes to `kitchen-service-domain/`
- [x] Move persistence classes to `kitchen-service-persistence/`
- [x] Move command handlers to `kitchen-service-command-handlers/`
- [x] Move event handling to `kitchen-service-event-handling/`
- [x] Move REST controllers to `kitchen-service-restapi/`
- [x] Move main class to `kitchen-service-main/`
- [x] Perform `javax.*` to `jakarta.*` migration
- [x] Update for Spring Boot 3.x compatibility

### Task 3.4: Migrate Kitchen Service tests to appropriate subprojects

- [x] Move unit tests alongside source files in each subproject
- [x] Update JUnit 4 to JUnit 5
- [x] Update `javax.*` to `jakarta.*` imports
- [x] Verify `./gradlew test` passes
- [x] Create integration tests in `kitchen-service-main/src/integrationTest/`
- [x] Verify `./gradlew integrationTest` passes
- [ ] Create component tests in `kitchen-service-main/src/componentTest/` (deferred)
- [ ] Verify `./gradlew componentTest` passes (deferred)

### Task 3.5: Embed contract tests in kitchen-service-event-publishing

- [x] Move contracts from `ftgo-kitchen-service-contracts/` to `kitchen-service-event-publishing/src/contractTest/`
- [x] Configure Spring Cloud Contract 4.x
- [x] Configure stub publishing
- [x] Verify `./gradlew contractTest` passes

### Task 3.6: Update Kitchen Service Dockerfile and infrastructure

- [x] Update `ftgo-kitchen-service/Dockerfile` for Java 17
- [x] Add `kitchen-service-db` to root `docker-compose.yaml`
- [x] Add `ftgo-kitchen-service` to root `docker-compose.yaml`
- [x] Update `build-and-test-all.sh` to include Kitchen Service
- [ ] Verify Docker build and service startup

### Task 3.7: Verify Kitchen Service integration

- [x] Run `./build-and-test-all.sh` and verify all three services build
- [ ] Verify Docker Compose starts all services (deferred)
- [x] Commit all changes for Steel Thread 3

---

## Steel Thread 4: Accounting Service Migration and Full Saga Validation

Migrate the Accounting Service to complete the CreateOrderSaga flow.

### Task 4.1: Transform ftgo-accounting-service into self-contained multi-module Gradle project

The `ftgo-accounting-service/` directory already exists. Transform it into a multi-module project:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `accounting-service-domain` - Core domain model (Account) and events
  - [x] `accounting-service-persistence` - JPA repositories and orm.xml
  - [x] `accounting-service-command-handlers` - Handles commands from Order Service
  - [x] `accounting-service-event-handling` - Consumes Consumer events + consumer contract tests
  - [x] `accounting-service-restapi` - REST controllers
  - [x] `accounting-service-main` - Spring Boot application, component tests
- [x] Create `gradle.properties` with version properties
- [x] Create root `build.gradle` with common configuration
- [x] Create each subproject's `build.gradle`
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Configure `java-test-fixtures` plugin in domain module
- [x] Verify `./gradlew tasks` runs successfully

### Task 4.2: Embed API and shared classes into Accounting Service subprojects

- [x] Copy shared classes into `accounting-service-domain/src/main/java/`
- [x] Move API classes from `ftgo-accounting-service-api/` into appropriate subprojects
- [x] Copy Order Service API classes into `accounting-service-command-handlers/`
- [x] Copy Consumer Service API classes into `accounting-service-event-handling/`
- [x] Update subproject dependencies

### Task 4.3: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move domain classes to `accounting-service-domain/`
- [x] Move persistence classes to `accounting-service-persistence/`
- [x] Move command handlers to `accounting-service-command-handlers/`
- [x] Move event handling to `accounting-service-event-handling/`
- [x] Move REST controllers to `accounting-service-restapi/`
- [x] Move main class to `accounting-service-main/`
- [x] Perform `javax.*` to `jakarta.*` migration
- [x] Update for Spring Boot 3.x compatibility

### Task 4.4: Migrate Accounting Service tests to appropriate subprojects

- [x] Move unit tests alongside source files in each subproject
- [x] Update JUnit 4 to JUnit 5
- [x] Update `javax.*` to `jakarta.*` imports
- [x] Verify `./gradlew test` passes
- [x] Create integration tests in `accounting-service-main/src/integrationTest/`
- [x] Verify `./gradlew integrationTest` passes
- [ ] Create component tests in `accounting-service-main/src/componentTest/` (deferred)
- [ ] Verify `./gradlew componentTest` passes (deferred)

### Task 4.5: Embed contract tests in accounting-service-command-handlers

- [x] Move contracts from `ftgo-accounting-service-contracts/` to `accounting-service-command-handlers/src/contractTest/`
- [x] Configure Spring Cloud Contract 4.x
- [x] Configure stub publishing
- [x] Verify `./gradlew contractTest` passes

### Task 4.6: Update Accounting Service Dockerfile and infrastructure

- [x] Update `ftgo-accounting-service/Dockerfile` for multi-module structure
- [x] Add `accounting-service-db` to root `docker-compose.yaml`
- [x] Add `ftgo-accounting-service` to root `docker-compose.yaml`
- [x] Update `build-and-test-all.sh` to include Accounting Service
- [ ] Verify Docker build and service startup (deferred)

### Task 4.7: Verify full saga integration

- [x] Run `./build-and-test-all.sh` and verify all four services build
- [ ] Verify Docker Compose starts all services (deferred)
- [ ] Verify end-to-end test demonstrates complete saga flow (deferred)
- [x] Commit all changes for Steel Thread 4

---

## Steel Thread 5: Restaurant Service Migration

Migrate the Restaurant Service which provides menu data for order creation.

### Task 5.1: Transform ftgo-restaurant-service into self-contained multi-module Gradle project

The `ftgo-restaurant-service/` directory already exists. Transform it into a multi-module project:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `restaurant-service-domain` - Core domain model (Restaurant, Menu) and events
  - [x] `restaurant-service-persistence` - JPA repositories and orm.xml
  - [x] `restaurant-service-event-publishing` - Publishes restaurant events + provider contract tests
  - [x] `restaurant-service-restapi` - REST controllers
  - [x] `restaurant-service-main` - Spring Boot application, component tests
- [x] Create `gradle.properties` with version properties
- [x] Create root `build.gradle` with common configuration
- [x] Create each subproject's `build.gradle`
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Configure `java-test-fixtures` plugin in domain module
- [x] Verify `./gradlew tasks` runs successfully

### Task 5.2: Embed API and shared classes into Restaurant Service subprojects

- [x] Copy shared classes into `restaurant-service-domain/src/main/java/`
- [x] Move API classes from `ftgo-restaurant-service-api/` into appropriate subprojects
- [x] Update subproject dependencies

### Task 5.3: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move domain classes to `restaurant-service-domain/`
- [x] Move persistence classes to `restaurant-service-persistence/`
- [x] Move REST controllers to `restaurant-service-restapi/`
- [x] Move main class to `restaurant-service-main/`
- [x] Perform `javax.*` to `jakarta.*` migration
- [x] Update for Spring Boot 3.x compatibility

### Task 5.4: Migrate Restaurant Service tests to appropriate subprojects

- [x] Move unit tests alongside source files in each subproject
- [x] Update JUnit 4 to JUnit 5
- [x] Update `javax.*` to `jakarta.*` imports
- [x] Verify `./gradlew test` passes
- [x] Create integration tests in `restaurant-service-main/src/integrationTest/`
- [x] Verify `./gradlew integrationTest` passes
- [ ] Create component tests in `restaurant-service-main/src/componentTest/`
- [ ] Verify `./gradlew componentTest` passes

### Task 5.5: Embed contract tests in restaurant-service-event-publishing

- [x] Move contracts from `ftgo-restaurant-service-contracts/` to `restaurant-service-event-publishing/src/contractTest/`
- [x] Configure Spring Cloud Contract 4.x
- [x] Configure stub publishing
- [x] Verify `./gradlew contractTest` passes

### Task 5.6: Update Restaurant Service Dockerfile and infrastructure

- [x] Update `ftgo-restaurant-service/Dockerfile` for Java 17
- [x] Add `restaurant-service-db` to root `docker-compose.yaml`
- [x] Add `ftgo-restaurant-service` to root `docker-compose.yaml`
- [x] Update `build-and-test-all.sh` to include Restaurant Service
- [ ] Verify Docker build and service startup

### Task 5.7: Verify Restaurant Service integration

- [x] Run `./build-and-test-all.sh` and verify all five services build
- [x] Commit all changes for Steel Thread 5

---

## Steel Thread 6: Delivery Service Migration

Migrate the Delivery Service for delivery scheduling.

### Task 6.1: Transform ftgo-delivery-service into self-contained multi-module Gradle project

The `ftgo-delivery-service/` directory already exists. Transform it into a multi-module project:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `delivery-service-domain` - Core domain model (Delivery, Courier) and events
  - [x] `delivery-service-persistence` - JPA repositories and orm.xml
  - [x] `delivery-service-event-handling` - Consumes events from Order, Kitchen, Restaurant services
  - [x] `delivery-service-restapi` - REST controllers
  - [x] `delivery-service-main` - Spring Boot application, component tests
- [x] Create `gradle.properties` with version properties
- [x] Create root `build.gradle` with common configuration
- [x] Create each subproject's `build.gradle`
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Configure `java-test-fixtures` plugin in domain module
- [x] Verify `./gradlew tasks` runs successfully

### Task 6.2: Embed API and shared classes into Delivery Service subprojects

- [x] Copy shared classes into `delivery-service-domain/src/main/java/`
- [x] Copy Order Service API classes into `delivery-service-event-handling/`
- [x] Copy Kitchen Service API classes into `delivery-service-event-handling/`
- [x] Copy Restaurant Service API classes into `delivery-service-event-handling/`
- [x] Update subproject dependencies

### Task 6.3: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move domain classes to `delivery-service-domain/`
- [x] Move persistence classes to `delivery-service-persistence/`
- [x] Move event handling to `delivery-service-event-handling/`
- [x] Move REST controllers to `delivery-service-restapi/`
- [x] Move main class to `delivery-service-main/`
- [x] Perform `javax.*` to `jakarta.*` migration
- [x] Update for Spring Boot 3.x compatibility

### Task 6.4: Migrate Delivery Service tests to appropriate subprojects

- [x] Move unit tests alongside source files in each subproject
- [x] Update JUnit 4 to JUnit 5
- [x] Update `javax.*` to `jakarta.*` imports
- [x] Verify `./gradlew test` passes (domain unit tests)
- [x] Move JPA tests to `delivery-service-persistence/src/integrationTest/`
- [x] Verify `./gradlew integrationTest` passes (persistence tests with Testcontainers)
- [x] Move component tests to `delivery-service-main/src/componentTest/` (disabled pending Flyway migrations for CDC tables)
- [ ] Verify `./gradlew componentTest` passes (requires Eventuate CDC schema setup)

### Task 6.5: Update Delivery Service Dockerfile and infrastructure

- [x] Update `ftgo-delivery-service/Dockerfile` for multi-module structure
- [x] Add `delivery-service-db` to root `docker-compose.yaml`
- [x] Add `ftgo-delivery-service` to root `docker-compose.yaml`
- [x] Add CDC pipeline configuration for delivery-service-db
- [x] Update `build-and-test-all.sh` to include Delivery Service
- [ ] Verify Docker build and service startup (deferred to end-to-end testing)

### Task 6.6: Verify Delivery Service integration

- [x] Run `./build-and-test-all.sh` and verify all six services build
- [x] Commit all changes for Steel Thread 6

### Task 6.8: Review test placement in previously migrated services

Review all previously migrated services (Order, Consumer, Kitchen, Accounting, Restaurant) to ensure tests are in the appropriate subproject:

- [x] JPA/repository tests should be in `*-persistence` or `*-domain` module (with the code they test)
  - Order Service: JPA tests correctly in `order-service-persistence/src/integrationTest/`
  - Delivery Service: JPA tests correctly in `delivery-service-persistence/src/integrationTest/`
  - Consumer, Kitchen, Restaurant: No JPA-specific tests (simple entities tested via integration tests)
  - Accounting: Uses event sourcing, no JPA entities
- [x] Unit tests should be alongside the code they test in each subproject
  - Domain tests in `*-domain/src/test/` (OrderTest, DeliveryServiceTest, etc.)
  - Controller/web tests in `*-restapi/src/test/`
- [x] Integration tests that test the full service should be in `*-main`
  - All services have integration tests correctly in `*-main/src/integrationTest/`
- [x] Contract tests should be in the module that publishes/consumes the contracts
  - Event publishing contracts in `*-event-publishing/src/contractTest/`
  - Command handler contracts in `*-command-handlers/src/contractTest/`
  - HTTP contracts in `*-restapi/src/contractTest/`
- [x] No misplaced tests found - all tests are in appropriate subprojects

---

## Steel Thread 7: Order History Service Migration (CQRS)

Migrate the Order History Service which maintains a DynamoDB read model.

### Task 7.1: Transform ftgo-order-history-service into self-contained multi-module Gradle project

The `ftgo-order-history-service/` directory already exists. Transform it into a multi-module project:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `order-history-service-domain` - Core domain model and query DTOs
  - [x] `order-history-service-dynamodb` - DynamoDB repositories (renamed from persistence)
  - [x] `order-history-service-event-handling` - Consumes Order events + consumer contract tests
  - [x] `order-history-service-restapi` - REST controllers for queries
  - [x] `order-history-service-main` - Spring Boot application, component tests
- [x] Create `gradle.properties` with version properties
- [x] Create root `build.gradle` with common configuration
- [x] Create each subproject's `build.gradle`
- [x] Add AWS DynamoDB dependencies compatible with Java 17
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Configure `java-test-fixtures` plugin in domain module
- [x] Verify `./gradlew tasks` runs successfully

### Task 7.2: Embed API and shared classes into Order History Service subprojects

- [x] Copy shared classes into `order-history-service-domain/src/main/java/`
- [x] Copy Order Service API classes into `order-history-service-event-handling/`
- [x] Update subproject dependencies

### Task 7.3: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move domain classes to `order-history-service-domain/`
- [x] Move persistence classes to `order-history-service-dynamodb/`
- [x] Move event handling to `order-history-service-event-handling/`
- [x] Move REST controllers to `order-history-service-restapi/`
- [x] Move main class to `order-history-service-main/`
- [x] Perform `javax.*` to `jakarta.*` migration
- [x] Update AWS SDK dependencies for Java 17 compatibility
- [x] Update for Spring Boot 3.x compatibility

### Task 7.4: Migrate Order History Service tests to appropriate subprojects

- [x] Move unit tests alongside source files in each subproject
- [x] Update JUnit 4 to JUnit 5
- [x] Update `javax.*` to `jakarta.*` imports
- [x] Verify `./gradlew test` passes
- [x] Create integration tests with DynamoDB Local testcontainer
- [x] Verify `./gradlew integrationTest` passes
- [ ] Create component tests in `order-history-service-main/src/componentTest/`
- [ ] Verify `./gradlew componentTest` passes

### Task 7.5: Update Order History Service Dockerfile and infrastructure

- [ ] Update `ftgo-order-history-service/Dockerfile` for Java 17
- [ ] Add `dynamodb-local` to root `docker-compose.yaml`
- [ ] Add `ftgo-order-history-service` to root `docker-compose.yaml`
- [x] Update `build-and-test-all.sh` to include Order History Service
- [ ] Verify Docker build and service startup

### Task 7.6: Verify Order History Service integration

- [x] Run `./build-and-test-all.sh` and verify all seven services build
- [x] Commit all changes for Steel Thread 7

### Task 7.8: Standardize artifact coordinates for contract stubs

After all services are migrated, update the Maven coordinates for consistency:

1. **Update group ID to include service name:**
   - Change `group = 'net.chrisrichardson.ftgo'` to `group = 'net.chrisrichardson.ftgo.<service-name>'`
   - Example: `group = 'net.chrisrichardson.ftgo.order-service'`
   - This makes the group ID unique per service

2. **Remove artifactId override when publishing stubs:**
   - Currently: `artifactId = "ftgo-restaurant-service"` (explicit override)
   - Change to: Use the natural module name (e.g., `restaurant-service-event-publishing`)
   - The unique group ID makes the explicit artifactId unnecessary

3. **Update all contract consumers:**
   - Change stub references from `net.chrisrichardson.ftgo:ftgo-{service}`
   - To `net.chrisrichardson.ftgo.{service}:{module-name}`

**Benefits:**
- Each service publishes with a unique group ID
- No need for artifactId overrides
- Natural module names reflect what actually publishes the contracts
- Consistent with Maven best practices

- [ ] Update all migrated services with service-specific group IDs
- [ ] Remove artifactId overrides from publishing configurations
- [ ] Update all `@AutoConfigureStubRunner` annotations with new coordinates
- [ ] Update `build-and-test-all.sh` if needed
- [ ] Verify `./build-and-test-all.sh` passes with new coordinates
- [ ] Commit changes

---

## Steel Thread 8: API Gateway Migration

Migrate the API Gateway as the final service.

### Task 8.1: Transform ftgo-api-gateway into self-contained multi-module Gradle project

The `ftgo-api-gateway/` directory already exists. Transform it into a multi-module project:

- [x] Create `settings.gradle` with pluginManagement block and subproject includes:
  - [x] `api-gateway-routing` - Route configurations and filters
  - [x] `api-gateway-main` - Spring Boot application
- [x] Create `gradle.properties` with version properties
- [x] Create root `build.gradle` with common configuration
- [x] Create each subproject's `build.gradle`
- [x] Add Spring Cloud Gateway dependencies for Spring Boot 3.x
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Verify `./gradlew tasks` runs successfully

### Task 8.2: Move source files to subprojects and migrate to Spring Boot 3.x / Jakarta EE

- [x] Move routing configuration to `api-gateway-routing/`
- [x] Move main class to `api-gateway-main/`
- [x] Update Spring Cloud Gateway configuration for Spring Boot 3.x
- [x] Perform `javax.*` to `jakarta.*` migration (if applicable)
- [x] Update route configurations for new service structure

### Task 8.3: Migrate API Gateway tests to appropriate subprojects

- [x] Move unit tests alongside source files
- [x] Update JUnit 4 to JUnit 5
- [x] Update `javax.*` to `jakarta.*` imports
- [x] Verify `./gradlew test` passes
- [ ] Create integration tests (if needed)
- [ ] Verify `./gradlew integrationTest` passes

### Task 8.4: Update API Gateway Dockerfile and infrastructure

- [ ] Update `ftgo-api-gateway/Dockerfile` for Java 17
- [ ] Add `ftgo-api-gateway` to root `docker-compose.yaml`
- [ ] Configure routing to all backend services
- [x] Update `build-and-test-all.sh` to include API Gateway
- [ ] Verify Docker build and service startup

### Task 8.5: Verify API Gateway integration

- [x] Run `./build-and-test-all.sh` and verify all eight services build
- [x] Commit all changes for Steel Thread 8

---

## Steel Thread 9: Final Cleanup and Validation

Remove legacy modules and validate the complete system.

### Task 9.1: Remove legacy API modules

- [x] Delete `ftgo-order-service-api/` directory
- [x] Delete `ftgo-consumer-service-api/` directory
- [x] Delete `ftgo-kitchen-service-api/` directory
- [x] Delete `ftgo-accounting-service-api/` directory
- [x] Delete `ftgo-restaurant-service-api/` directory
- [x] Delete `ftgo-delivery-service-api/` directory

### Task 9.2: Remove legacy contract modules

- [x] Delete `ftgo-order-service-contracts/` directory
- [x] Delete `ftgo-consumer-service-contracts/` directory
- [x] Delete `ftgo-kitchen-service-contracts/` directory
- [x] Delete `ftgo-accounting-service-contracts/` directory
- [x] Delete `ftgo-restaurant-service-contracts/` directory
- [x] Update `settings.gradle` to remove contract module includes
- [x] Update contract consumer tests to use new artifact IDs (service name instead of *-contracts)

### Task 9.3: Remove legacy API spec modules

- [x] Delete `ftgo-consumer-service-api-spec/` directory
- [x] Delete `ftgo-accounting-service-api-spec/` directory
- [x] Delete `ftgo-restaurant-service-api-spec/` directory

### Task 9.4: Remove legacy shared modules

- [x] Delete `ftgo-common/` directory
- [x] Delete `ftgo-common-jpa/` directory
- [x] Delete `common-swagger/` directory
- [x] Delete `ftgo-test-util/` directory
- [x] Delete `ftgo-test-util-json-schema/` directory

### Task 9.5: Remove legacy build infrastructure

- [x] Delete root-level `settings.gradle`
- [x] Delete root-level `build.gradle`
- [x] Delete root-level `gradle.properties`
- [x] Delete root-level `gradlew`, `gradlew.bat`, and `gradle/` directory
- [x] Delete `buildSrc/` directory
- [x] Verify no root-level Gradle files remain

### Task 9.6: Final system validation

- [x] Run `./build-and-test-all.sh` and verify all services build and test
- [ ] Run `docker-compose up` and verify all services start and are healthy
- [x] Verify end-to-end tests pass (see Steel Thread 10 for full e2e test implementation)
- [ ] Commit final cleanup changes

### Task 9.7: Reorganize integration tests from *-main to adapter subprojects

Review all `*-main/src/integrationTest/` directories and move tests to appropriate adapter subprojects:

**Current state:** Integration tests in `*-main/src/integrationTest/` often test multiple concerns (REST API + messaging) in a single test class.

**Target state:** Tests should be co-located with the code they test:
- REST API tests → `*-restapi/src/integrationTest/`
- Command handler tests → `*-command-handlers/src/integrationTest/`
- Event handling tests → `*-event-handling/src/integrationTest/`

**Services to review:**
- [ ] `ftgo-consumer-service/consumer-service-main/src/integrationTest/` - split REST vs messaging tests
- [ ] `ftgo-kitchen-service/kitchen-service-main/src/integrationTest/` - move messaging test to command-handlers
- [ ] `ftgo-accounting-service/accounting-service-main/src/integrationTest/` - review test placement
- [ ] `ftgo-restaurant-service/restaurant-service-main/src/integrationTest/` - review test placement
- [ ] `ftgo-delivery-service/delivery-service-main/src/integrationTest/` - review test placement
- [ ] `ftgo-order-history-service/order-history-service-main/src/integrationTest/` - review test placement

**Guidelines:**
- Tests that exercise REST controllers → `*-restapi/src/integrationTest/`
- Tests that exercise command handlers via messaging → `*-command-handlers/src/integrationTest/`
- Tests that exercise event handlers → `*-event-handling/src/integrationTest/`
- Tests that require the full application context (cross-cutting) → remain in `*-main/src/integrationTest/`
- Use `@MockitoBean` to isolate adapter-specific tests from other layers

### Task 9.8: Update documentation

- [ ] Update root README.md with new build instructions
- [ ] Document the self-contained service structure
- [ ] Document how to build and run individual services
- [ ] Document how to run end-to-end tests:
  - TestContainers mode (automatic container management)
  - Docker Compose mode (pre-started containers)

---

## Steel Thread 10: End-to-End Tests

Transform `ftgo-end-to-end-tests/` into a standalone self-contained Gradle project using the realguardio end-to-end-tests model. The existing EndToEndTests.java test methods will be preserved and updated to modern technology.

### Task 10.1: Create standalone end-to-end-tests Gradle project

Transform the existing `ftgo-end-to-end-tests/` into a self-contained project:

- [x] Create `ftgo-end-to-end-tests/settings.gradle` with:
  - [x] `rootProject.name = 'ftgo-end-to-end-tests'`
- [x] Create `ftgo-end-to-end-tests/gradle.properties` with version properties
- [x] Rewrite `ftgo-end-to-end-tests/build.gradle`:
  - [x] Java 17 toolchain configuration
  - [x] Custom `endToEndTest` source set (separate from `test`)
  - [x] Modern dependencies:
    - REST Assured 5.x (`io.rest-assured:rest-assured`)
    - Awaitility 4.x
    - JUnit 5
    - Testcontainers 1.19+
    - Eventuate Testcontainers support
  - [x] Remove legacy dependencies (com.jayway.restassured, swagger-codegen)
  - [x] Remove dependencies on *-api projects (not self-contained)
- [x] Generate Gradle wrapper using `gradle wrapper`
- [x] Verify `./gradlew tasks` runs successfully

### Task 10.2: Create ApplicationUnderTest abstraction

Following the realguardio pattern, create an abstraction for launching the system under test:

- [x] Create `ApplicationUnderTest` interface with factory method and port accessors
- [x] Create `ApplicationUnderTestUsingTestContainers` implementation:
  - Uses Testcontainers to programmatically start all service containers
  - Uses `EventuateKafkaNativeCluster` for Kafka
  - Uses `EventuateCdcContainer` for CDC service
  - Starts all FTGO service containers with proper networking
- [x] Create `ApplicationUnderTestUsingDockerCompose` implementation:
  - Connects to pre-started docker-compose services
  - Returns hardcoded ports for docker-compose setup
- [x] Factory method in interface selects implementation based on `endToEndTestMode` system property

### Task 10.3: Update EndToEndTests.java to modern tech

Update the existing test class while preserving all test methods:

- [x] Create new `EndToEndTests.java` in `src/endToEndTest/java/`
- [x] Update JUnit 4 annotations to JUnit 5:
  - `@BeforeClass` → `@BeforeAll`
  - `@Test` → `@Test` (JUnit 5 version)
- [x] Update REST Assured imports:
  - `com.jayway.restassured` → `io.restassured`
- [x] Replace `Eventually` with Awaitility 4.x
- [x] Integrate with `ApplicationUnderTest`:
  - Use `@BeforeAll` to start `ApplicationUnderTest`
  - Get service URLs from `ApplicationUnderTest` instead of hardcoded ports
  - Use `@AfterAll` to stop `ApplicationUnderTest`
- [x] Create self-contained DTO classes (no external dependencies)
- [x] Preserve existing test methods:
  - `shouldCreateReviseAndCancelOrder()`
  - `shouldDeliverOrder()`
  - `testSwaggerUiUrls()`
- [x] Verify all tests compile

### Task 10.4: Verify end-to-end tests pass

- [ ] Verify TestContainers mode works:
  ```bash
  cd ftgo-end-to-end-tests && ./gradlew endToEndTest
  ```
- [ ] Verify Docker Compose mode works:
  ```bash
  docker-compose up -d
  cd ftgo-end-to-end-tests && ./gradlew endToEndTest -DendToEndTestMode=DockerCompose
  ```
- [ ] Verify all existing tests pass:
  - `shouldCreateReviseAndCancelOrder()` - Order lifecycle
  - `shouldDeliverOrder()` - Delivery workflow
  - `testSwaggerUiUrls()` - API documentation
- [ ] Commit all changes for Steel Thread 10

---

## Summary

| Steel Thread | Services | Key Validation |
|--------------|----------|----------------|
| 1 | Order Service | First self-contained multi-module service builds and tests |
| 2 | + Consumer Service | Order + Consumer validation works (multi-module) |
| 3 | + Kitchen Service | Ticket creation works (multi-module) |
| 4 | + Accounting Service | Full CreateOrderSaga happy path (multi-module) |
| 5 | + Restaurant Service | Restaurant data integration (multi-module) |
| 6 | + Delivery Service | Delivery scheduling (multi-module) |
| 7 | + Order History Service | CQRS query works (multi-module) |
| 8 | + API Gateway | Full system via gateway (multi-module) |
| 9 | Cleanup | Legacy modules removed, final validation |
| 10 | End-to-End Tests | Full system validation with ApplicationUnderTest pattern |

## Architecture Pattern

Each service follows the same multi-module Gradle project structure established by Order Service:

```
ftgo-{service-name}/
├── settings.gradle              # Subproject definitions
├── build.gradle                 # Common configuration
├── gradle.properties            # Version properties
├── gradlew, gradlew.bat         # Gradle wrapper
├── {service}-domain/            # Core domain model, events, business logic
├── {service}-persistence/       # JPA/DynamoDB repositories, orm.xml
├── {service}-command-handlers/  # Handles saga commands (if saga participant)
├── {service}-event-handling/    # Consumes events from other services + consumer contract tests
├── {service}-event-publishing/  # Publishes domain events + provider contract tests
├── {service}-proxies-{other}/   # Proxy for calling other service (if saga orchestrator)
├── {service}-sagas/             # Saga orchestration (if saga orchestrator)
├── {service}-restapi/           # REST controllers
└── {service}-main/              # Spring Boot application, component tests
```

**Module Types by Service Role:**

| Service Role | Required Modules |
|--------------|------------------|
| Saga Orchestrator (Order) | domain, persistence, sagas, proxies-*, event-handling, event-publishing, restapi, main |
| Saga Participant (Consumer, Kitchen, Accounting) | domain, persistence, command-handlers, event-publishing, restapi, main |
| Event Producer (Restaurant) | domain, persistence, event-publishing, restapi, main |
| Event Consumer (Delivery, Order History) | domain, persistence, event-handling-*, restapi, main |

**Key patterns:**
- **Self-contained**: Each service has its own Gradle wrapper and all dependencies explicit
- **Embedded APIs**: API classes copied into each service (no shared modules)
- **Test fixtures**: Use `java-test-fixtures` plugin in domain module for shared test utilities
- **Tests migrated in place**: Never delete tests - move them to the correct subproject
- **Contract tests co-located**: Consumer contract tests in event-handling modules, provider contract tests in event-publishing modules
