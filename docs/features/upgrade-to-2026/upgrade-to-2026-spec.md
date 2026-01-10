# FTGO Application Upgrade to 2026 - Platform Capability Specification

## Purpose and Context

### Background

The FTGO (Food To Go) application is the companion example codebase for the "Microservices Patterns" book by Chris Richardson. It demonstrates microservices architecture patterns including sagas, CQRS, event sourcing, and API composition using the Eventuate platform.

The codebase currently uses outdated technologies:
- Java 8 (end-of-life)
- Spring Boot 2.2.6 (end-of-life)
- Gradle 6.9.1 (legacy)
- Deprecated dependencies (JCenter, Swagger, javax namespace)

### Purpose

Modernize the FTGO application to use current, supported technologies while restructuring the project to follow modern microservices best practices. This ensures the educational codebase remains relevant for readers learning microservices development.

### Target State

| Component | Current | Target |
|-----------|---------|--------|
| Java | 8 | 17 (via Gradle toolchain) |
| Gradle | 6.9.1 | 8.13 |
| Spring Boot | 2.2.6.RELEASE | 3.4.0 |
| Spring Cloud | Hoxton | 2024.0.0 |
| Spring Cloud Contract | 2.2.0.RELEASE | 4.2.0 |
| Eventuate Platform | 2023.1.RELEASE | 2025.1.BUILD-SNAPSHOT |
| JUnit | 4/5 mixed | 5.x (JUnit Platform) |
| API Documentation | Swagger | SpringDoc OpenAPI |
| Repository | JCenter + Maven Central | Maven Central only |
| Build Configuration | `compile` configuration | `implementation` configuration |

---

## Consumers

### Primary Consumers

1. **Book Readers** - Developers learning microservices patterns from the "Microservices Patterns" book who need working, modern example code to study and run.

2. **Workshop Attendees** - Participants in microservices training who use FTGO as hands-on lab material.

3. **Self-Learners** - Developers exploring microservices architecture who clone and experiment with the codebase.

### Secondary Consumers

4. **CI/CD Systems** - Automated build and test pipelines that validate the codebase.

5. **Eventuate Framework Developers** - Use FTGO as a reference implementation for Eventuate features.

---

## Capabilities and Behaviors

### C1: Self-Contained Service Projects

Each Spring Boot service must be a fully self-contained Gradle project that can be built, tested, and deployed independently.

**Requirements:**
- Each service has its own `settings.gradle`, `build.gradle`, `gradle.properties`, and `gradlew`
- No root-level Gradle build file
- Services contain all necessary source code (no cross-service compile dependencies)
- Shared domain types (Money, Address, PersonName, etc.) duplicated into each service
- API classes embedded within each service

**Services in scope (8 total):**

| Service | Description |
|---------|-------------|
| `ftgo-order-service` | Order management and saga orchestration |
| `ftgo-consumer-service` | Consumer/customer management |
| `ftgo-kitchen-service` | Kitchen ticket management |
| `ftgo-accounting-service` | Payment authorization |
| `ftgo-restaurant-service` | Restaurant and menu management |
| `ftgo-delivery-service` | Delivery scheduling and tracking |
| `ftgo-order-history-service` | CQRS read model (DynamoDB) |
| `ftgo-api-gateway` | Spring Cloud Gateway routing |

**Explicitly excluded:**
- `ftgo-restaurant-service-aws-lambda`
- `ftgo-api-gateway-graphql`

### C2: Modern Build Configuration

Each service must use modern Gradle and Spring Boot patterns.

**Requirements:**
- Gradle 8.13 with Gradle wrapper
- Java 17 via Gradle toolchain configuration
- `java-library` plugin with `implementation`/`api` configurations
- Maven Central repository only (no JCenter)
- Platform BOMs for dependency management:
  - `io.eventuate.platform:eventuate-platform-dependencies`
  - `org.springframework.boot:spring-boot-dependencies`
  - `org.springframework.cloud:spring-cloud-dependencies`

**Testing plugins:** Use published Eventuate testing plugins (NOT custom buildSrc plugins):

```gradle
// settings.gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url 'https://snapshots.repositories.eventuate.io/repository' }
    }
    plugins {
        id 'io.eventuate.plugins.gradle.testing.integration-tests' version "${eventuateTestPluginsVersion}"
        id 'io.eventuate.plugins.gradle.testing.component-tests' version "${eventuateTestPluginsVersion}"
    }
}

// build.gradle (in service)
plugins {
    id 'io.eventuate.plugins.gradle.testing.component-tests'
    id 'io.eventuate.plugins.gradle.testing.integration-tests'
}

componentTest {
    systemProperty "spring.profiles.active", "test,postgres"
}
integrationTest {
    systemProperty "spring.profiles.active", "test,postgres"
}
```

These plugins provide:
- `componentTestImplementation`, `integrationTestImplementation` configurations
- `componentTest`, `integrationTest` tasks
- Test source sets at `src/componentTest/java` and `src/integrationTest/java`

**Test infrastructure:** Integration and component tests must use **Testcontainers** (NOT Docker Compose plugin):

Current FTGO approach (to be replaced):
- Uses `com.avast.gradle:gradle-docker-compose-plugin`
- Requires running `docker-compose up` before tests
- Tests depend on `integrationTestsComposeUp` / `componentTestsComposeUp` tasks

Target approach (from reference projects):
- Uses Testcontainers to programmatically start containers
- Tests are self-contained and start their own infrastructure
- Eventuate provides testcontainer support modules

Example test dependencies:
```gradle
componentTestImplementation 'org.testcontainers:postgresql'
componentTestImplementation 'org.testcontainers:junit-jupiter'
componentTestImplementation 'io.eventuate.messaging.kafka:eventuate-messaging-kafka-testcontainers'
componentTestImplementation 'io.eventuate.common:eventuate-common-testcontainers'
componentTestImplementation 'io.eventuate.platform.testcontainer.support:eventuate-platform-testcontainer-support-service'
```

### C3: Spring Boot 3.x Compatibility

All services must run on Spring Boot 3.4.0 with Jakarta EE.

**Requirements:**
- Complete `javax.*` to `jakarta.*` namespace migration
- Spring Boot 3.x compatible annotations and configurations
- SpringDoc OpenAPI replacing Swagger/SpringFox
- Updated Spring Security patterns (if applicable)
- JUnit 5 Platform for all tests

### C4: Embedded Contract Tests

Contract tests must be embedded within each service following Spring Cloud Contract 4.x patterns.

**Requirements:**
- Contracts in `src/contractTest/resources/contracts/`
- Contract test base classes in `src/contractTest/java/`
- Stubs published to shared `build/repos/contracts/` directory
- Consumer services reference stubs from shared repository
- Contract tests run as part of service build

### C5: Root-Level Orchestration

The root level provides orchestration without being a Gradle project.

**Requirements:**
- `docker-compose.yaml` at root level orchestrating all services
- Docker Compose builds service images from local Dockerfiles
- `build-and-test-all.sh` script to build and test all migrated services
- No root-level `settings.gradle` or `build.gradle`

**Critical constraint:** Unmigrated services are NOT compiled or tested. The old monolithic Gradle build is abandoned. Only migrated self-contained services are built via their own `./gradlew build`.

### C6: End-to-End Test Project

A standalone `end-to-end-tests` Gradle project validates cross-service behavior.

**Requirements:**
- Own `settings.gradle`, `build.gradle`, `gradlew`
- JUnit 5 + RestAssured + Testcontainers
- Eventuate testcontainer support
- Two modes: Testcontainers mode and Docker Compose mode
- Tests only exercise migrated services

### C7: Docker Support

All services must support containerized deployment with Java 17.

**Requirements:**
- Dockerfile per service using `eventuateio/eventuate-examples-docker-images-spring-example-base-image`
- Base image supports Java 17
- Optional multi-stage builds using `amazoncorretto:17` for build stage
- Health check endpoints via Spring Boot Actuator

---

## High-Level APIs, Contracts, and Integration Points

### Service Communication

Services communicate via:

1. **Eventuate Tram Messaging** - Asynchronous command/reply and domain events via Kafka
2. **Eventuate Tram Sagas** - Saga orchestration for distributed transactions
3. **REST APIs** - Synchronous HTTP endpoints for queries and commands

### Contract Stubs Repository

```
build/
  repos/
    contracts/
      net.chrisrichardson.ftgo/
        ftgo-order-service/
          stubs.jar
        ftgo-consumer-service/
          stubs.jar
        ...
```

### Build Script Interface

```bash
# Build and test all migrated services
./build-and-test-all.sh

# Build individual service
cd ftgo-order-service && ./gradlew build

# Run end-to-end tests
cd end-to-end-tests && ./gradlew endToEndTest
```

### Docker Compose Services

The root `docker-compose.yaml` defines:

| Service | Purpose |
|---------|---------|
| `*-db` | PostgreSQL databases per service |
| `kafka` | Confluent Kafka (KRaft mode) |
| `zookeeper` | (if needed) |
| `cdc-service` | Eventuate CDC for outbox pattern |
| `ftgo-*-service` | Application services |
| `dynamodb-local` | Local DynamoDB for order-history-service |

---

## Non-Functional Requirements and SLAs

### Build Performance

- Individual service build: < 5 minutes (including tests)
- Full system build (`build-and-test-all.sh`): < 30 minutes
- Incremental builds should leverage Gradle build cache

### Test Coverage

- All existing tests must pass after migration
- Test types: unit, integration, component, contract, end-to-end
- No reduction in test coverage

### Compatibility

- Must run on macOS (Apple Silicon and Intel) and Linux
- Docker Desktop or compatible container runtime required
- Gradle wrapper eliminates local Gradle installation requirement

### Documentation

- Code should be self-explanatory for educational purposes
- Build and run instructions in README
- No additional documentation changes required for this upgrade

---

## Scenarios and Workflows

### Primary End-to-End Scenario: CreateOrderSaga Flow

This scenario validates the core saga pattern works with all upgraded dependencies.

**Participants (in invocation order):**
1. `ftgo-order-service` - Saga orchestrator
2. `ftgo-consumer-service` - Consumer validation
3. `ftgo-kitchen-service` - Ticket creation
4. `ftgo-accounting-service` - Payment authorization

**Flow:**
1. Order Service receives create order request
2. Order Service starts CreateOrderSaga
3. Saga sends ValidateOrderByConsumer command to Consumer Service
4. Consumer Service validates and replies
5. Saga sends CreateTicket command to Kitchen Service
6. Kitchen Service creates ticket and replies
7. Saga sends AuthorizeCard command to Accounting Service
8. Accounting Service authorizes and replies
9. Saga sends ConfirmCreateTicket to Kitchen Service
10. Saga approves the order
11. Order is created successfully

**Validation:**
- All saga participants communicate via Eventuate Tram
- Commands and events serialize/deserialize correctly with Jakarta EE
- Database transactions work with updated JPA/Hibernate
- End-to-end test confirms order reaches APPROVED state

### Secondary Scenarios

#### S2: Restaurant Data Flow
- Restaurant Service provides menu data
- Order Service uses restaurant data for order creation

#### S3: Delivery Scheduling
- Delivery Service schedules courier
- Integrates with order lifecycle events

#### S4: Order History (CQRS)
- Order History Service consumes order events
- Maintains DynamoDB read model
- Supports order queries

#### S5: API Gateway Routing
- API Gateway routes requests to services
- Spring Cloud Gateway configuration works with Spring Boot 3.x

---

## Migration Order

Services must be migrated in the following order, based on CreateOrderSaga invocation flow:

### Phase 1: Saga Participants

| Order | Service | Rationale |
|-------|---------|-----------|
| 1 | `ftgo-order-service` | Saga orchestrator, starting point |
| 2 | `ftgo-consumer-service` | First saga participant (validateOrder) |
| 3 | `ftgo-kitchen-service` | Second saga participant (create/confirm ticket) |
| 4 | `ftgo-accounting-service` | Third saga participant (authorize) |

### Phase 2: Remaining Services

| Order | Service | Rationale |
|-------|---------|-----------|
| 5 | `ftgo-restaurant-service` | Provides restaurant data |
| 6 | `ftgo-delivery-service` | Delivery management |
| 7 | `ftgo-order-history-service` | CQRS read model |
| 8 | `ftgo-api-gateway` | Routing layer |

---

## Legacy Module Disposition

When a service is migrated, its associated `-api` and `-contracts` modules are absorbed and then deleted from the root level.

### API Modules (`ftgo-*-service-api`)

**Current state:** Separate modules at root level containing commands, events, and DTOs used for inter-service communication.

**After migration:**

1. **Copied INTO the parent service** - The service's own API classes are embedded within the service itself
2. **Copied INTO consuming services** - When a consuming service is migrated, it receives copies of the API classes it needs to communicate with other services
3. **Original module DELETED** - The root-level `-api` module is removed

**Example: `ftgo-order-service-api`**

| Destination | What gets copied | When |
|-------------|------------------|------|
| `ftgo-order-service` | All API classes (own service's commands, events, DTOs) | When Order Service is migrated |
| `ftgo-kitchen-service` | Classes needed to receive commands from Order Service | When Kitchen Service is migrated |
| `ftgo-consumer-service` | Classes needed to receive commands from Order Service | When Consumer Service is migrated |
| `ftgo-accounting-service` | Classes needed to receive commands from Order Service | When Accounting Service is migrated |

After all consuming services are migrated, `ftgo-order-service-api` is deleted from the root level.

### Contract Modules (`ftgo-*-service-contracts`)

**Current state:** Separate modules at root level containing Spring Cloud Contract definitions.

**After migration:**

1. **Contracts moved INTO the service** - Contract definitions move to `src/contractTest/resources/contracts/`
2. **Base classes moved INTO the service** - Contract test base classes move to `src/contractTest/java/`
3. **Stubs published to shared repository** - Stubs go to `build/repos/contracts/` for consumer services
4. **Original module DELETED** - The root-level `-contracts` module is removed

**Example: `ftgo-order-service-contracts`**

| Content | Destination | When |
|---------|-------------|------|
| Contract `.groovy` files | `ftgo-order-service/src/contractTest/resources/contracts/` | When Order Service is migrated |
| Base test classes | `ftgo-order-service/src/contractTest/java/` | When Order Service is migrated |
| Generated stubs | `build/repos/contracts/net.chrisrichardson.ftgo/ftgo-order-service/` | At build time |

After migration, `ftgo-order-service-contracts` is deleted from the root level.

### API Spec Modules (`ftgo-*-service-api-spec`)

**Current state:** JSON schema definitions for message types.

**After migration:**

1. **Schemas embedded in consuming services** - JSON schemas or generated classes copied to services that need them
2. **Code generation updated** - jsonschema2pojo or similar generates classes within each service
3. **Original module DELETED** - The root-level `-api-spec` module is removed

### Summary: Modules Deleted After Full Migration

Once all 8 services are migrated, these root-level modules no longer exist:

| Module Type | Examples |
|-------------|----------|
| API modules | `ftgo-order-service-api`, `ftgo-kitchen-service-api`, `ftgo-consumer-service-api`, `ftgo-restaurant-service-api`, `ftgo-accounting-service-api` |
| Contract modules | `ftgo-order-service-contracts`, `ftgo-kitchen-service-contracts`, `ftgo-consumer-service-contracts`, `ftgo-accounting-service-contracts` |
| API spec modules | `ftgo-consumer-service-api-spec`, `ftgo-accounting-service-api-spec`, `ftgo-restaurant-service-api-spec` |
| Shared modules | `ftgo-common`, `ftgo-common-jpa`, `common-swagger`, `ftgo-test-util` |

---

## Incremental Testing Approach

End-to-end tests are written incrementally as each service is migrated.

| After Migrating | E2E Test Coverage |
|-----------------|-------------------|
| Order Service | Basic order creation (stub other services) |
| Consumer Service | Order + consumer validation |
| Kitchen Service | Order + consumer + ticket creation |
| Accounting Service | Full CreateOrderSaga happy path |
| Restaurant Service | Restaurant data integration |
| Delivery Service | Delivery scheduling |
| Order History Service | CQRS query validation |
| API Gateway | Full system via gateway |

**Constraint:** A service is NOT tested until it is migrated. Tests only exercise migrated services.

---

## Constraints and Assumptions

### Constraints

1. **No functional changes** - Application behavior must remain identical
2. **Flat service structure** - Services remain single Gradle projects (no internal subprojects)
3. **No cross-service compile dependencies** - Services are fully isolated at build time
4. **Incremental migration** - Services migrated one at a time following saga order
5. **Eventuate compatibility** - Must use Eventuate Platform 2025.1.BUILD-SNAPSHOT
6. **Unmigrated services not compiled/tested** - The old monolithic Gradle build is abandoned; only migrated services are built and tested via their self-contained `./gradlew build`

### Assumptions

1. Eventuate base Docker image already supports Java 17
2. All Eventuate dependencies are compatible with Spring Boot 3.4.0
3. DynamoDB Local works with Java 17
4. Reference projects (`eventuate-tram-examples-customers-and-orders`, `eventuate-examples-realguardio`) are functional and can be used as templates
5. Existing test logic is correct and only framework/API updates are needed

---

## Acceptance Criteria

### Per-Service Criteria

For each migrated service:

- [ ] Service compiles with Java 17 and Gradle 8.13
- [ ] All `javax.*` imports replaced with `jakarta.*`
- [ ] Service is self-contained (own `settings.gradle`, `build.gradle`, `gradlew`)
- [ ] All shared code duplicated into service
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Component tests pass
- [ ] Contract tests pass and stubs published
- [ ] Service starts and responds to health checks
- [ ] Docker image builds successfully

### System-Level Criteria

- [ ] All 8 services migrated and passing tests
- [ ] `build-and-test-all.sh` executes successfully
- [ ] `docker-compose.yaml` starts all services
- [ ] End-to-end tests pass (full CreateOrderSaga flow)
- [ ] No root-level Gradle build files exist
- [ ] Code follows modern best practices visible in reference projects

### Final Validation

- [ ] Complete CreateOrderSaga executes successfully:
  - Order created
  - Consumer validated
  - Ticket created
  - Payment authorized
  - Order approved
- [ ] Order appears in Order History Service
- [ ] API Gateway successfully routes to all services

---

## Reference Projects

These projects serve as templates for the migration:

| Project | Path | Purpose |
|---------|------|---------|
| eventuate-tram-examples-customers-and-orders | `/Users/cer/src/eventuate-examples/eventuate-tram-examples-customers-and-orders` | Build configuration, contract tests |
| eventuate-tram-sagas-examples-customers-and-orders | `/Users/cer/src/eventuate-examples/eventuate-tram-sagas-examples-customers-and-orders` | Saga patterns with modern dependencies |
| eventuate-examples-realguardio | `/Users/cer/src/eventuate-examples/eventuate-examples-realguardio` | Self-contained service structure, Docker setup |

---

## Change History

### 2026-01-09: Integration/component tests to use Testcontainers

- Tests must use Testcontainers instead of Docker Compose plugin
- Replaces `com.avast.gradle:gradle-docker-compose-plugin` approach
- Tests become self-contained, programmatically starting their own infrastructure
- Added example Testcontainer dependencies from reference projects

### 2026-01-09: Added Eventuate testing plugin configuration

- Added details on using published Eventuate testing plugins instead of custom buildSrc plugins
- Plugins: `io.eventuate.plugins.gradle.testing.integration-tests` and `io.eventuate.plugins.gradle.testing.component-tests`
- Includes example configuration from realguardio reference project

### 2026-01-09: Clarified unmigrated services not compiled/tested

- Added constraint: unmigrated services are NOT compiled or tested
- Old monolithic Gradle build is abandoned during migration
- Only migrated self-contained services are built via their own `./gradlew build`

### 2026-01-09: Added Legacy Module Disposition section

- Added detailed explanation of what happens to `-api`, `-contracts`, and `-api-spec` modules
- Clarified that API classes are copied into consuming services when they are migrated
- Clarified that contracts are embedded in services and original modules are deleted
- Listed all modules that will be deleted after full migration

### 2026-01-09: Initial specification created

- Created Platform Capability Specification based on discussion log
- Captured all 16 decisions from brainstorming session
- Defined migration order following CreateOrderSaga flow
- Specified incremental testing approach
- Documented acceptance criteria for per-service and system-level validation
