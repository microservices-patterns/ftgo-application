# FTGO Order Service Migration Analysis

## Overview

This document analyzes the migration patterns and strategies applied to modernize `ftgo-order-service` from a monolithic multi-module Gradle subproject to a self-contained Spring Boot 3.x service. The migration occurred across 18 commits from January 10-11, 2026.

**Migration Scope:**
- Java 8 → Java 17
- Spring Boot 2.2.6 → Spring Boot 3.4.0
- javax.* → jakarta.* (Jakarta EE 10)
- JUnit 4 → JUnit 5
- Docker Compose-based tests → Testcontainers
- CircleCI → GitHub Actions
- Monolithic build → Self-contained Gradle project

---

## Key Migration Strategies

### 1. Self-Contained Service Architecture

**Strategy:** Transform the service from a subproject of a monolithic Gradle build into a fully independent project with its own Gradle wrapper, dependencies, and build configuration.

**Implementation:**
- Created standalone `settings.gradle` with pluginManagement block
- Created `gradle.properties` with all version properties (previously inherited from root)
- Rewrote `build.gradle` to be self-contained with explicit dependencies
- Added Gradle 8.13 wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`)

**Commits:** `5388f1e`, `58658d8`

**Benefits:**
- Each service can be built and tested independently
- Version upgrades can proceed service-by-service
- Reduced coupling between services
- Enables per-service CI/CD pipelines

---

### 2. Dependency Embedding (API Internalization)

**Strategy:** Copy external API classes and shared utilities directly into the service rather than depending on shared modules.

**Implementation:**
- Copied classes from `ftgo-order-service-api/` into `orderservice/api/`
- Copied common utilities (Money, Address, PersonName) from `ftgo-common/`
- Copied JPA mappings (`orm.xml`) from `ftgo-common-jpa/`
- Copied API classes from other services (consumer, kitchen, accounting, restaurant) that Order Service depends on

**Commits:** `e66f409`

**Files Affected:** 47 new files copied into the service

**Trade-offs:**
- **Pro:** Complete independence from external modules
- **Pro:** No version coordination needed across modules
- **Con:** Potential code duplication across services
- **Con:** API changes require updating in multiple places

**Mitigation:** This is intentional duplication for the migration period. Long-term, services communicate only via messaging contracts.

---

### 3. Phased Namespace Migration (javax → jakarta)

**Strategy:** Perform the javax.* to jakarta.* migration as a dedicated commit, separate from other changes.

**Implementation:**
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.annotation.*` → `jakarta.annotation.*`
- `javax.servlet.*` → `jakarta.servlet.*`
- Updated `orm.xml` to Jakarta Persistence 3.0 namespace

**Commits:** `b17c6df` (source code), `18855a0` (tests)

**Best Practice:** Isolating namespace changes in dedicated commits makes it easy to:
- Review all migration points
- Bisect if issues arise
- Understand the scope of changes

---

### 4. Deprecated API Removal

**Strategy:** Remove deprecated or incompatible features rather than attempting complex migrations.

**Removed Components:**
- `TraceIdResponseFilter` (used deprecated Spring Cloud Sleuth, replaced by Micrometer Tracing)
- `grpc` package (requires separate protobuf setup, deferred)
- Legacy Swagger/SpringFox annotations (not present in this service)

**Commits:** `b17c6df`

**Rationale:** Removing deprecated code reduces migration complexity and technical debt. Features can be re-added later with modern implementations if needed.

---

### 5. Test Migration Strategy

**Strategy:** Migrate tests in layers, from unit tests (fastest feedback) to component tests (full integration).

**Phase 1: Unit Tests**
- Replace JUnit 4 annotations with JUnit 5 equivalents
- Update `javax.annotation` to `jakarta.annotation`
- Update `Mockito.Matchers` to `Mockito.ArgumentMatchers`
- Verify with `./gradlew test`

**Commits:** `18855a0`

**Phase 2: Integration Tests with Testcontainers**
- Create `OrderRepositoryIntegrationTest` with PostgreSQL testcontainer
- Use `@DataJpaTest` with `@Testcontainers` annotation
- Configure dynamic properties for database connection
- Verify with `./gradlew integrationTest`

**Commits:** `8b0391c`, `70c3b86`

**Phase 3: Component Tests with Full Stack**
- Create `OrderServiceComponentTest` using Eventuate Kafka and PostgreSQL testcontainers
- Test REST API endpoints (GET /orders, actuator health)
- Verify with `./gradlew componentTest`

**Commits:** `354ddaa`

---

### 6. Multi-Module Decomposition

**Strategy:** Split the single-module service into 10 focused subprojects for better separation of concerns.

**Module Structure:**
```
ftgo-order-service/
├── order-service-domain/                      # Core domain model, events, business logic
├── order-service-persistence/                 # JPA repositories, orm.xml
├── order-service-sagas/                       # Saga orchestration (CreateOrderSaga, CancelOrderSaga, ReviseOrderSaga)
├── order-service-proxies-kitchen-service/     # Proxy for Kitchen Service + consumer contract tests
├── order-service-proxies-accounting-service/  # Proxy for Accounting Service + consumer contract tests
├── order-service-proxies-consumer-service/    # Proxy for Consumer Service + consumer contract tests
├── order-service-event-handling/              # Consumes Restaurant events + consumer contract tests
├── order-service-event-publishing/            # Publishes Order events + provider contract tests
├── order-service-restapi/                     # REST controllers
└── order-service-main/                        # Spring Boot application, component tests
```

**Commits:** `bf90e80`

**Module Responsibilities:**

| Module | Purpose |
|--------|---------|
| domain | Order aggregate, domain events, business logic, test fixtures |
| persistence | OrderRepository, RestaurantRepository, orm.xml |
| sagas | CreateOrderSaga, CancelOrderSaga, ReviseOrderSaga orchestration |
| proxies-kitchen-service | KitchenServiceProxy, consumer contract tests for Kitchen replies |
| proxies-accounting-service | AccountingServiceProxy, consumer contract tests for Accounting replies |
| proxies-consumer-service | ConsumerServiceProxy, consumer contract tests for Consumer replies |
| event-handling | RestaurantEventConsumer, consumer contract tests for Restaurant events |
| event-publishing | OrderDomainEventPublisher, provider contract tests for Order events |
| restapi | OrderController, RestaurantController |
| main | OrderServiceMain, component tests, Cucumber tests |

**Benefits:**
- Clear dependency graph (proxies depend on domain, main depends on all)
- Each module can be tested in isolation
- Enables fine-grained dependency management
- Contract tests naturally align with proxy/event-handling modules

---

### 7. Test Fixtures with Gradle Plugin

**Strategy:** Use Gradle's `java-test-fixtures` plugin instead of `sourceSets.test.output` for sharing test utilities.

**Implementation:**
```groovy
// order-service-domain/build.gradle
plugins {
    id 'java-test-fixtures'
}

dependencies {
    testFixturesImplementation project(':order-service-domain')
    testFixturesImplementation 'org.junit.jupiter:junit-jupiter-api'
}
```

**Commits:** `8b89819`

**Benefits:**
- Proper Gradle API instead of anti-pattern
- Works correctly in CI environments
- Explicit dependency on fixtures from consuming modules

---

### 8. Contract Testing Modernization

**Strategy:** Reorganize contract projects as standalone Gradle projects with modern Spring Cloud Contract 4.x patterns.

**Contract Project Structure:**
```
ftgo-kitchen-service-contracts/
├── gradle wrapper files
├── build.gradle (Spring Cloud Contract 4.2.0)
└── src/
    ├── main/java/*/contracts/MessagingBase.java
    └── contractTest/resources/contracts/
        └── order-service/
            └── replies/
                ├── createTicketReply.groovy
                └── confirmCreateTicketReply.groovy
```

**Consumer-Side Contract Tests:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
    ids = "net.chrisrichardson.ftgo:ftgo-kitchen-service-contracts:+:stubs",
    stubsMode = StubRunnerProperties.StubsMode.REMOTE
)
@DirtiesContext
public class KitchenServiceContractTest {

    @Autowired
    private StubFinder stubFinder;

    @Test
    public void shouldReceiveCreateTicketReply() {
        stubFinder.trigger("createTicket");
    }
}
```

**Commits:** `65970bf` (plan), `e96f809` (implementation)

**Key Patterns:**
- Per-consumer contract organization (`order-service/replies/`)
- `triggeredBy()` pattern in contract definitions
- `@EnableEventuateTramContractVerifier` for Eventuate integration
- Stub publishing to local Maven repository before service builds

---

### 9. CI/CD Pipeline Modernization

**Strategy:** Replace CircleCI with GitHub Actions, using a simple orchestration script.

**Build Script (`build-and-test-all.sh`):**
1. Publish contract stubs from all contract projects
2. Build and test each migrated service

**GitHub Actions Workflow:**
```yaml
- name: Build and Test
  run: ./build-and-test-all.sh --build-cache
```

**Commits:** `bf229f4` (CircleCI → GitHub Actions), `f9e4670` (Gradle build cache)

**Benefits:**
- Simpler configuration
- Gradle build cache for faster builds
- Artifact upload for test reports

---

### 10. Test Migration (Lesson Learned)

**What Happened:** During the multi-module restructuring (`bf90e80`), several tests were accidentally deleted and had to be restored in a later commit (`74a7ff2`).

**Tests That Were Removed and Later Restored:**
- OrderEventConsumerTest (unit)
- OrderServiceIntegrationTest, KitchenServiceProxyIntegrationTest (integration)
- HttpBase, MessagingBase, DeliveryserviceMessagingBase (contracts)
- Cucumber OrderServiceComponentTest with place-order.feature

**Commits:** `bf90e80` (accidental removal), `74a7ff2` (restoration)

**Lesson Learned:** Tests should be migrated in place, not removed. The correct approach is:
1. Move test files to their new module locations
2. Update imports and dependencies
3. Verify tests still pass after the move
4. Never delete tests during restructuring - if a test fails after migration, fix it

---

## Migration Sequence Summary

| Phase | Commits | Description |
|-------|---------|-------------|
| 1. Planning | `a16623c` | Document migration plan and specification |
| 2. Infrastructure | `58658d8` | Root-level build script and docker-compose |
| 3. Standalone Project | `5388f1e` | Gradle wrapper, settings, properties |
| 4. Embed Dependencies | `e66f409` | Copy API and shared classes |
| 5. Spring Boot 3.x | `b17c6df` | javax→jakarta, API updates |
| 6. Unit Tests | `18855a0` | JUnit 5, jakarta namespace |
| 7. Integration Tests | `8b0391c`, `70c3b86` | Testcontainers for PostgreSQL |
| 8. Component Tests | `354ddaa` | Testcontainers for full stack |
| 9. Multi-Module | `bf90e80` | Split into 10 subprojects |
| 10. Test Fix | `74a7ff2` | Restore accidentally deleted tests |
| 11. CI/CD | `bf229f4`, `f9e4670` | GitHub Actions, build cache |
| 12. Contract Testing | `65970bf`, `e96f809` | Modern contract patterns |
| 13. Test Fixtures | `8b89819` | java-test-fixtures plugin |
| 14. Fixes | `59218cc` | Case-sensitive file names for Linux |

---

## Lessons Learned

### What Worked Well

1. **Dedicated commits for specific changes** - Made review and debugging easier
2. **Self-contained services** - Eliminated version coordination issues
3. **Testcontainers adoption** - More reliable than Docker Compose plugin
4. **Multi-module decomposition** - Clear separation of concerns
5. **Contract stub publishing before builds** - Ensured consumer tests have stubs available

### Challenges Encountered

1. **Accidental test deletion** - Tests were removed during multi-module restructuring and had to be restored later. Tests should always be migrated in place, never deleted.
2. **Test fixture sharing** - Required switching from `sourceSets.test.output` to `java-test-fixtures`
3. **Case-sensitive file names** - Linux CI caught issues macOS missed
4. **Cucumber 7+ migration** - Required `io.cucumber.*` import changes
5. **gRPC deferred** - Requires separate protobuf setup, not included in initial migration

### Recommended Order for Future Services

1. Create standalone Gradle project structure (settings.gradle, gradle.properties, Gradle wrapper)
2. Embed API and shared dependencies (copy classes from API modules, ftgo-common, ftgo-common-jpa)
3. Migrate source code (javax→jakarta)
4. Migrate unit tests (JUnit 5, jakarta namespace) - fastest feedback loop
5. Migrate integration tests with Testcontainers
6. Migrate component tests with Testcontainers
7. Restructure into multi-module Gradle project (mandatory - see Architecture Pattern in upgrade-to-2026-plan.md)
8. Update contract tests (consumer tests in event-handling, provider tests in event-publishing)
9. Update build-and-test-all.sh and docker-compose.yaml
10. Final validation and cleanup
