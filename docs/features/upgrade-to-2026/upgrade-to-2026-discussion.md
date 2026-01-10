# Upgrade to 2026 - Discussion Log

## Overview

This document captures the discussion and decisions made while refining the idea to upgrade the FTGO application codebase to modern versions of Gradle, Java, and dependencies.

## Current State Analysis

### FTGO Application (Current)
- **Gradle**: 6.9.1
- **Java**: 1.8 (source/target compatibility)
- **Spring Boot**: 2.2.6.RELEASE
- **Spring Cloud Contract**: 2.2.0.RELEASE
- **Eventuate Platform**: 2023.1.RELEASE
- **Repository**: Uses deprecated `jcenter()` repository
- **Build Style**: Legacy `buildscript` block with `compile` configurations

### Reference Projects (Target)
- **Gradle**: 8.13
- **Java**: 17 (via toolchain)
- **Spring Boot**: 3.4.0
- **Spring Cloud**: 2024.0.0
- **Spring Cloud Contract**: 4.2.0
- **Eventuate Platform**: 2025.1.BUILD-SNAPSHOT
- **JUnit**: 5.x with JUnit Platform
- **Build Style**: Modern `java-library` plugin, `implementation` configurations

### Key Differences Identified
1. Java 8 → Java 17 (major version jump)
2. Spring Boot 2.x → 3.x (major upgrade, Jakarta EE migration)
3. Gradle 6.x → 8.x (configuration cache, new APIs)
4. JCenter deprecated → Maven Central only
5. JUnit 4/5 mixed → JUnit 5 Platform
6. Swagger → SpringDoc OpenAPI

---

## Questions and Answers

### Q1: What is the main motivation for this upgrade?

**Answer: C - Educational/Book Update**

This codebase accompanies the "Microservices Patterns" book and needs updating to remain relevant for readers learning modern microservices development.

**Implications:**
- Code should reflect current best practices that readers would use in production
- The upgrade should be thorough enough that readers don't encounter outdated patterns
- Documentation and examples should be clear for educational purposes

---

### Q2: How do you want to approach the upgrade scope?

**Answer: A - Full alignment with reference projects**

The FTGO application should match the modern reference projects exactly:
- Spring Boot 3.4.0
- Gradle 8.13
- Java 17 (via toolchain)
- All dependencies updated to latest compatible versions

**Implications:**
- Complete javax → jakarta namespace migration required
- All deprecated APIs must be replaced
- Build configuration modernized (java-library plugin, implementation configurations)
- JCenter removed, Maven Central only

---

### Q3: What is the scope of this upgrade phase?

**Answer: Focus on Spring Boot applications first**

This upgrade will focus on the core Spring Boot Java services. The following are explicitly deferred to later phases:
- `ftgo-restaurant-service-aws-lambda` (AWS Lambda module)
- `ftgo-api-gateway-graphql` (Node.js/TypeScript GraphQL gateway)
- Any other non-Spring Boot components

---

### Q4: What is the test expectation for validating the upgrade?

**Answer: A - All tests must pass**

The upgrade is complete only when all existing tests pass:
- Unit tests
- Integration tests
- Component tests
- Contract tests
- End-to-end tests

**Implications:**
- Test framework must be migrated to JUnit 5 Platform
- Spring Boot Test updates (e.g., `@SpringBootTest` changes)
- Any test utilities using deprecated APIs must be updated
- Test containers and Docker Compose test infrastructure must work with updated services

---

### Q5: Which Eventuate Platform version should be used?

**Answer: A - Use the latest snapshot**

Match the reference projects exactly with `eventuatePlatformVersion=2025.1.BUILD-SNAPSHOT`.

**Implications:**
- Snapshots may change, but this ensures compatibility with reference project patterns
- The educational codebase will demonstrate the latest Eventuate features
- May need periodic updates as snapshots evolve

---

### Q6: Project structure - self-contained services

**Answer: Each Spring Boot application should be a self-contained Gradle project**

Reference: `/Users/cer/src/eventuate-examples/eventuate-examples-realguardio`

The realguardio pattern shows:
- **No root-level Gradle build** - each service is independent
- **Each service has its own**: `settings.gradle`, `build.gradle`, `gradle.properties`, `gradlew`
- **Internal subprojects per service**: domain, persistence, api, restapi, main, etc.
- **Cross-service dependencies**: via `includeBuild()` with dependency substitution

This is a significant refactoring beyond version upgrades - it restructures the entire project organization.

---

### Q7: How should shared API modules be handled?

**Answer: C - Embedded in each service**

Each service contains its own API subproject, even if code is similar across services.

**Implications:**
- Maximum isolation - each service is fully self-contained
- No cross-service Gradle dependencies at build time
- Some code duplication for shared domain concepts (e.g., `Money`, `Address`)
- Cleaner for educational purposes - readers can understand one service without understanding others
- Services communicate only via messaging/events at runtime, not shared compile-time dependencies

---

### Q8: Docker base image for Java 17

**Answer: A - Assume base image is already updated (derived from reference projects)**

Both reference projects (`eventuate-tram-examples-customers-and-orders` and `eventuate-examples-realguardio`) continue to use:
```
eventuateio/eventuate-examples-docker-images-spring-example-base-image:$baseImageVersion
```

Since these projects use Java 17 toolchain, the base image must already support Java 17.

**Additional pattern from realguardio** - multi-stage builds use `amazoncorretto:17` for build, eventuate base image for runtime.

---

### Q9: How should cross-service concerns be organized?

**Answer: Follow realguardio pattern (derived from reference project)**

The realguardio project structure:

**Root level (NOT a Gradle project):**
- `docker-compose.yaml` - orchestrates all services, databases, Kafka, Jaeger, etc.
- Individual service directories (each standalone Gradle project)

**`end-to-end-tests/` - Standalone Gradle project:**
- Own `settings.gradle`, `build.gradle`, `gradlew`
- Uses Testcontainers + Eventuate testcontainer support
- Has `assembleServices` task that calls `./gradlew assemble` in each service directory via Exec tasks
- Supports two modes:
  - **Testcontainers mode** - spins up containers programmatically
  - **Docker Compose mode** - uses root docker-compose.yaml
- Can use `includeBuild()` to reference service test-container modules if needed

**Key patterns:**
- Services are built independently via Exec tasks, not Gradle composite builds
- End-to-end tests have their own JUnit 5 + RestAssured + Testcontainers setup
- docker-compose.yaml builds service images from local Dockerfiles

---

### Q10: Which Spring Boot applications are in scope?

**Answer: A - All listed services + API gateway**

**In scope (8 applications):**
1. `ftgo-order-service`
2. `ftgo-kitchen-service`
3. `ftgo-restaurant-service`
4. `ftgo-consumer-service`
5. `ftgo-accounting-service`
6. `ftgo-delivery-service`
7. `ftgo-order-history-service`
8. `ftgo-api-gateway` (Spring Cloud Gateway)

**Explicitly excluded:**
- `ftgo-restaurant-service-aws-lambda`
- `ftgo-api-gateway-graphql`

---

### Q11: Internal structure of each service?

**Answer: A - Keep flat structure**

Each service remains a single Gradle project (not split into internal subprojects like realguardio).

**Implications:**
- Simpler migration path - restructure project boundaries, not internal architecture
- Matches existing book content better
- Each service is self-contained with its own `build.gradle`, `settings.gradle`, `gradlew`
- No domain/persistence/api/main subproject separation within services

---

### Q12: How should shared modules be handled?

**Answer: A - Duplicate into each service**

Current shared modules (`ftgo-common`, `ftgo-common-jpa`, `common-swagger`, `ftgo-test-util`) will have their needed classes copied into each service that uses them.

**Implications:**
- Full isolation - no external dependencies between services at build time
- Some code duplication for common types (Money, Address, PersonName, etc.)
- Each service is truly self-contained and can be understood in isolation
- Changes to common types in one service don't affect others
- Aligns with microservices principle of independent deployability

---

### Q13: How should contract tests be handled?

**Answer: A - Embed contracts in each service**

Move contracts into `src/contractTest/` within each service, publish stubs to a shared local repository (following the reference project pattern).

**Implications:**
- Contracts live alongside the service code they define
- Each service publishes stubs to a shared `build/repos/contracts/` directory
- Consumer services reference stubs from this shared repository
- Follows Spring Cloud Contract 4.x patterns
- Contract tests run as part of each service's build

---

## Classification

**Type: C - Platform/Infrastructure Capability**

**Rationale:**
This work is primarily a platform/infrastructure upgrade rather than a user-facing feature or architecture POC:

1. **Not a user-facing feature** - No new functionality is being added; the application behavior remains the same
2. **Not an architecture POC** - The microservices architecture is already proven; this validates the upgrade path
3. **Platform capability** - Modernizes the build system, runtime platform (Java 17), and dependency versions
4. **Educational aspect** - While the codebase is educational, the work itself is infrastructure modernization

The upgrade enables the educational codebase to remain relevant by running on current, supported platforms.

---

### Q14: In what order should services be migrated?

**Answer: Follow CreateOrderSaga invocation order**

Based on the `CreateOrderSaga.java`, services should be migrated in the order they participate in the saga:

**Phase 1 - Saga participants (in invocation order):**
1. `ftgo-order-service` - Saga orchestrator (starting point)
2. `ftgo-consumer-service` - validateOrder
3. `ftgo-kitchen-service` - create/confirmCreate ticket
4. `ftgo-accounting-service` - authorize

**Phase 2 - Remaining services:**
5. `ftgo-restaurant-service` - provides restaurant data
6. `ftgo-delivery-service` - delivery management
7. `ftgo-order-history-service` - CQRS read model (DynamoDB)
8. `ftgo-api-gateway` - routing layer

**Rationale:**
- Order Service is the core orchestrator, migrate first
- Following saga flow ensures dependencies are migrated in logical order
- Validates the saga pattern works with upgraded dependencies early
- Remaining services can be migrated in parallel once core saga works

---

### Q15: When should end-to-end tests be written?

**Answer: Incrementally, as each service is migrated**

End-to-end tests should be written and expanded incrementally as each service is migrated, not saved for the end.

**Implications:**
- Create `end-to-end-tests` project early (after first service migration)
- Add tests that exercise each service as it's migrated
- Tests grow progressively to cover more of the saga flow
- Early validation that migrated services integrate correctly
- By the time all services are migrated, comprehensive e2e coverage exists

**Example progression:**
1. After Order Service: Basic order creation test (may stub other services)
2. After Consumer Service: Order + consumer validation test
3. After Kitchen Service: Order + consumer + ticket creation test
4. After Accounting Service: Full CreateOrderSaga happy path test
5. After remaining services: Complete system tests

**Important constraint:** A service should not be tested until it is migrated. Tests only exercise migrated services - no testing of old/non-migrated service versions.

---

### Q16: How should all services be built and tested together?

**Answer: build-and-test-all.sh script**

A `build-and-test-all.sh` script at the root level that builds and tests each migrated service by running `./gradlew build` in each service directory.

**Implications:**
- Simple shell script that iterates over service directories
- Runs `./gradlew build` (which includes tests) for each service
- Script grows as services are migrated
- Provides single command to validate entire system
- No root-level Gradle build needed - just a shell script

**Example structure:**
```bash
#!/bin/bash
set -e

# Build and test each migrated service
for service in ftgo-order-service ftgo-consumer-service ...; do
  echo "Building $service..."
  (cd $service && ./gradlew build)
done

# Run end-to-end tests
(cd end-to-end-tests && ./gradlew endToEndTest)
```

---

## Final Confirmation

**Q: Are there any additional requirements or concerns before moving to specification?**

**Answer: No**

All requirements have been captured. Ready to proceed to the detailed specification phase.

---

## Post-Specification Clarifications

### Clarification: What happens to `-api` and `-contracts` modules?

**Question:** After the order-service has been migrated, what's the impact on `ftgo-order-service-api` and `ftgo-order-service-contracts`?

**Answer:**

**`ftgo-order-service-api`:**
- API classes are copied INTO `ftgo-order-service` itself
- When consuming services are migrated (consumer, kitchen, accounting), they receive copies of the Order Service API classes they need
- The original `ftgo-order-service-api` module is DELETED from the root level after all consumers are migrated

**`ftgo-order-service-contracts`:**
- Contract definitions move to `ftgo-order-service/src/contractTest/resources/contracts/`
- Contract test base classes move to `ftgo-order-service/src/contractTest/java/`
- Stubs are published to `build/repos/contracts/`
- The original `ftgo-order-service-contracts` module is DELETED from the root level

This clarification has been added to the specification as the "Legacy Module Disposition" section.

---

### Clarification: Unmigrated services not compiled/tested

**Statement:** Unmigrated projects should not be compiled/tested.

**Implication:**
- The old monolithic Gradle build (root-level `settings.gradle` and `build.gradle`) is abandoned
- Only migrated self-contained services are built via their own `./gradlew build`
- `build-and-test-all.sh` only includes migrated services
- During migration, the codebase is in a transitional state where old and new structures coexist, but only the new structure is actively built

This has been added as constraint #6 in the specification.

---

### Clarification: Component and integration test plugins

**Reference:** See realguardio for how it configures component and integration plugins.

**Finding:** Realguardio uses **published Eventuate testing plugins** instead of custom buildSrc plugins:

```gradle
// settings.gradle
pluginManagement {
    plugins {
        id 'io.eventuate.plugins.gradle.testing.integration-tests' version "${eventuateTestPluginsVersion}"
        id 'io.eventuate.plugins.gradle.testing.component-tests' version "${eventuateTestPluginsVersion}"
    }
}

// build.gradle
plugins {
    id 'io.eventuate.plugins.gradle.testing.component-tests'
    id 'io.eventuate.plugins.gradle.testing.integration-tests'
}
```

**Implication:**
- FTGO services should use these published plugins, NOT custom buildSrc plugins
- The current `IntegrationTestsPlugin.groovy` and `ComponentTestsPlugin.groovy` in buildSrc are replaced
- Plugins provide `componentTestImplementation`, `integrationTestImplementation` configurations and corresponding tasks

This has been added to C2 (Modern Build Configuration) in the specification.

---

### Clarification: Tests should use Testcontainers

**Statement:** Integration and component tests should be migrated to use Testcontainers like the sample projects.

**Current FTGO approach (to be replaced):**
- Uses `com.avast.gradle:gradle-docker-compose-plugin`
- Tests depend on `integrationTestsComposeUp` / `componentTestsComposeUp` tasks
- Requires external Docker Compose orchestration

**Target approach (from reference projects):**
- Uses Testcontainers to programmatically start containers within tests
- Tests are fully self-contained
- Eventuate provides testcontainer support modules:
  - `eventuate-messaging-kafka-testcontainers`
  - `eventuate-common-testcontainers`
  - `eventuate-platform-testcontainer-support-service`

This has been added to C2 (Modern Build Configuration) in the specification.

---
