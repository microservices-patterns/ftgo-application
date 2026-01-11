# Contract Testing Implementation Plan for ftgo-order-service

## Overview

This plan outlines the reorganization of existing contract tests for `ftgo-order-service` based on patterns from:
- `eventuate-examples-realguardio` - Domain event contract testing
- `eventuate-tram-sagas-examples-customers-and-orders` - Saga command/reply contracts
- `eventuate-tram-spring-testing-support-cloud-contract-example-command-service` - Command service pattern

## Current State

### Existing Contract Projects

The following standalone contract projects **already exist**:

| Project | Existing Contracts |
|---------|-------------------|
| `ftgo-kitchen-service-contracts` | `CreateTicket.groovy`, `ConfirmCreateTicket.groovy`, `TicketAcceptedEvent.groovy` |
| `ftgo-accounting-service-contracts` | `Authorize.groovy` |
| `ftgo-consumer-service-contracts` | `VerifyConsumer.groovy` |
| `ftgo-restaurant-service-contracts` | `RestaurantCreatedEvent.groovy` |
| `ftgo-order-service-contracts` | `OrderCreatedEvent.groovy`, `GetOrder.groovy`, `GetNonExistentOrder.groovy` |

### Existing Base Classes in ftgo-order-service

| Module | File |
|--------|------|
| `order-service-event-handling` | `MessagingBase.java`, `DeliveryserviceMessagingBase.java` |
| `order-service-restapi` | `HttpBase.java` |

### Current Contract Structure (Old Pattern)

```
ftgo-kitchen-service-contracts/
└── src/main/resources/contracts/
    ├── messaging/
    │   ├── CreateTicket.groovy          # Uses messageFrom/messageBody pattern
    │   └── ConfirmCreateTicket.groovy
    └── deliveryservice/messaging/
        └── TicketAcceptedEvent.groovy
```

### Current build.gradle (Old Pattern)

```gradle
apply plugin: 'spring-cloud-contract'
apply plugin: 'maven-publish'

contracts {
    contractsDslDir = file("${projectDir}/src/main/resources/contracts")
}

generateContractTests.enabled = false  # Tests not generated!

dependencies {
    compile 'org.codehaus.groovy:groovy-all:2.4.6'  # Outdated
}
```

### Gaps to Address

- [ ] Contracts use old `messageFrom`/`messageBody` pattern instead of `triggeredBy`
- [ ] `generateContractTests.enabled = false` - provider-side tests not running
- [ ] No `@EnableEventuateTramContractVerifier` integration
- [ ] No per-consumer contract organization
- [ ] Outdated dependencies
- [ ] Consumer-side tests in ftgo-order-service not using `StubFinder.trigger()`

---

## Architecture

### Contract Testing Model

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ftgo-order-service (Saga Orchestrator)            │
│                         (Consumer of Contracts)                      │
│                                                                      │
│  CreateOrderSaga sends commands to:                                  │
│    - ConsumerService (ValidateOrderByConsumer)                       │
│    - KitchenService (CreateTicket, ConfirmCreateTicket, etc.)        │
│    - AccountingService (AuthorizeCommand)                            │
│                                                                      │
│  Subscribes to events from:                                          │
│    - RestaurantService (RestaurantCreated, RestaurantMenuRevised)    │
│                                                                      │
│  Publishes domain events consumed by:                                │
│    - delivery-service                                                │
│    - other services                                                  │
└─────────────────────────────────────────────────────────────────────┘
```

### Provider Services (Existing Contract Projects)

| Provider Project | Role | Existing Contracts |
|-----------------|------|-------------------|
| `ftgo-kitchen-service-contracts` | Saga Participant | `CreateTicket`, `ConfirmCreateTicket` |
| `ftgo-accounting-service-contracts` | Saga Participant | `Authorize` |
| `ftgo-consumer-service-contracts` | Saga Participant | `VerifyConsumer` |
| `ftgo-restaurant-service-contracts` | Event Publisher | `RestaurantCreatedEvent` |

---

## Implementation Tasks

### Phase 1: Reorganize Existing Contract Projects

#### 1.1 ftgo-kitchen-service-contracts

- [x] **1.1.1** Reorganize contracts into per-consumer structure
- [x] **1.1.2** Update contract syntax from `messageFrom` to `triggeredBy` pattern
- [x] **1.1.3** Update `build.gradle` with modern configuration (tests disabled, publish only)
- [x] **1.1.4** Add `MessagingBase.java` for contract verification (kept for future use)
- [x] **1.1.5** Add API classes locally (until provider service is migrated)

**Current Structure:**
```
src/main/resources/contracts/
├── messaging/
│   ├── CreateTicket.groovy
│   └── ConfirmCreateTicket.groovy
└── deliveryservice/messaging/
    └── TicketAcceptedEvent.groovy
```

**Target Structure:**
```
src/contractTest/resources/contracts/
├── order-service/
│   ├── commands/
│   │   ├── createTicketCommand.groovy
│   │   └── confirmCreateTicketCommand.groovy
│   └── replies/
│       ├── ticketCreatedReply.groovy
│       └── successReply.groovy
└── delivery-service/
    └── events/
        └── ticketAcceptedEvent.groovy
```

#### 1.2 ftgo-accounting-service-contracts

- [x] **1.2.1** Reorganize contracts into per-consumer structure
- [x] **1.2.2** Update contract syntax to `triggeredBy` pattern
- [x] **1.2.3** Update `build.gradle` (tests disabled, publish only)
- [x] **1.2.4** Add `MessagingBase.java` (kept for future use)

**Current:** `contracts/Authorize.groovy`
**Target:** `contracts/order-service/replies/authorizeReply.groovy`

#### 1.3 ftgo-consumer-service-contracts

- [x] **1.3.1** Reorganize contracts into per-consumer structure
- [x] **1.3.2** Update contract syntax to `triggeredBy` pattern
- [x] **1.3.3** Update `build.gradle` (tests disabled, publish only)
- [x] **1.3.4** Add `MessagingBase.java` (kept for future use)

**Current:** `contracts/VerifyConsumer.groovy`
**Target:** `contracts/order-service/replies/validateOrderByConsumerReply.groovy`

#### 1.4 ftgo-restaurant-service-contracts

- [x] **1.4.1** Reorganize contracts into per-consumer structure
- [x] **1.4.2** Update contract syntax to `triggeredBy` pattern
- [x] **1.4.3** Update `build.gradle` (tests disabled, publish only)
- [x] **1.4.4** Add `MessagingBase.java` (kept for future use)

**Current:** `contracts/deliveryservice/messaging/RestaurantCreatedEvent.groovy`
**Target:** `contracts/order-service/events/restaurantCreatedEvent.groovy`

### Phase 2: Update build-and-test-all.sh

- [x] **2.1** Add contract publishing step before service tests
- [x] **2.2** Ensure proper ordering of build tasks

**Important:** Only run the publish task - not the build task. Contract tests are disabled but kept for future use.

**Add to build-and-test-all.sh:**
```bash
# Publish all contract stubs first (publish only, not build)
echo "Publishing contract stubs..."
(cd ftgo-kitchen-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)
(cd ftgo-accounting-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)
(cd ftgo-consumer-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)
(cd ftgo-restaurant-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)
```

### Phase 3: Update Consumer-Side Tests in ftgo-order-service

**Note:** Simplified approach using regular `test` source set instead of `contractTest` source set. Tests use `StubFinder.trigger()` pattern with `@AutoConfigureStubRunner(stubsMode = REMOTE)`.

#### 3.1 order-service-proxies-kitchen-service

- [x] **3.1.1** Create test class using `StubFinder.trigger()` pattern
- [x] **3.1.2** Update `build.gradle` with stub runner dependencies and repositoryRoot system property

#### 3.2 order-service-proxies-accounting-service

- [x] **3.2.1** Create test class using `StubFinder.trigger()` pattern
- [x] **3.2.2** Update `build.gradle` with stub runner dependencies and repositoryRoot system property

#### 3.3 order-service-proxies-consumer-service

- [x] **3.3.1** Create test class using `StubFinder.trigger()` pattern
- [x] **3.3.2** Update `build.gradle` with stub runner dependencies and repositoryRoot system property

#### 3.4 order-service-event-handling

- [x] **3.4.1** Add consumer contract test for restaurant events using `StubFinder.trigger()`
- [x] **3.4.2** Update `build.gradle` with stub runner dependencies and repositoryRoot system property

### Phase 4: Update ftgo-order-service-contracts (Provider)

- [x] **4.1** Convert to standalone Gradle project with wrapper
- [x] **4.2** Reorganize contracts into per-consumer structure
- [x] **4.3** Create `MessagingBase.java` (disabled, kept for future use)
- [x] **4.4** Update `build.gradle` with modern configuration and stub publishing
- [x] **4.5** Add to `build-and-test-all.sh`

**Final Structure:**
```
src/contractTest/resources/contracts/
├── delivery-service/
│   └── events/
│       └── OrderCreatedEvent.groovy
├── default/
│   └── events/
│       └── OrderCreatedEvent.groovy
└── api-gateway/
    └── http/
        ├── GetOrder.groovy
        └── GetNonExistentOrder.groovy
```

### Phase 5: Build Integration

- [x] **5.1** Configure stub repository URL via `systemProperty "stubrunner.repositoryRoot"` in test tasks
- [x] **5.2** Updated `build-and-test-all.sh` to publish all contract stubs before building services
- [x] **5.3** Verified full build passes with all contract tests

---

## Contract Syntax Migration

### Old Pattern (Current)

```groovy
// ftgo-kitchen-service-contracts - CreateTicket.groovy (CURRENT)
org.springframework.cloud.contract.spec.Contract.make {
    label 'createTicket'
    input {
        messageFrom('kitchenService')
        messageBody('''{"orderId":99,"restaurantId":1,...}''')
        messageHeaders {
            header('command_type','...CreateTicket')
            header('command_reply_to', '...-reply')
        }
    }
    outputMessage {
        sentTo('...-reply')
        body([ticketId: 99])
        headers {
            header('reply_type', '...CreateTicketReply')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}
```

### New Pattern (Target)

```groovy
// ftgo-kitchen-service-contracts - createTicketCommand.groovy (TARGET)
org.springframework.cloud.contract.spec.Contract.make {
    label 'createTicket'
    input {
        triggeredBy('createTicket()')
    }
    outputMessage {
        sentTo('kitchenService')
        body([
            orderId: 99,
            restaurantId: 1,
            ticketDetails: [lineItems: [[menuItemId: '1', name: 'Chicken Vindaloo', quantity: 5]]]
        ])
        headers {
            header('command_type', 'net.chrisrichardson.ftgo.kitchenservice.api.CreateTicket')
            header('command_reply_to', 'createTicketReply')
        }
    }
}

// ticketCreatedReply.groovy (NEW - separate file for reply)
org.springframework.cloud.contract.spec.Contract.make {
    label 'ticketCreated'
    input {
        triggeredBy('ticketCreated()')
    }
    outputMessage {
        sentTo('createTicketReply')
        body([ticketId: 99])
        headers {
            header('reply_type', 'net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}
```

---

## File Templates

### Updated build.gradle for Contract Projects

```gradle
// ftgo-kitchen-service-contracts/build.gradle (UPDATED)

plugins {
    id 'java-library'
    id 'org.springframework.cloud.contract' version "$springCloudContractVersion"
    id 'maven-publish'
}

apply plugin: io.eventuate.tram.spring.testing.cloudcontract.plugins.gradle.PublishStubsPlugin

group = 'net.chrisrichardson.ftgo'
version = project.version

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation platform("org.springframework.cloud:spring-cloud-contract-dependencies:$springCloudContractDependenciesVersion")
    implementation "io.eventuate.tram.core:eventuate-tram-commands:$eventuateTramVersion"

    contractTestImplementation "io.eventuate.tram.testingsupport.springcloudcontract:eventuate-tram-spring-testing-support-cloud-contract:$eventuateTramSpringTestingSupportCloudContractVersion"
    contractTestImplementation "org.springframework.cloud:spring-cloud-starter-contract-verifier"
    contractTestImplementation "org.springframework.boot:spring-boot-starter-test:$springBootVersion"
}

contracts {
    testFramework = "JUNIT5"
    baseClassForTests = "net.chrisrichardson.ftgo.kitchenservice.contracts.MessagingBase"
    // Move contracts to contractTest source set
    contractsDslDir = file("${projectDir}/src/contractTest/resources/contracts")
}

contractTest {
    useJUnitPlatform()
    enabled = false  // Tests disabled for now, will be enabled when provider service is migrated
}

tasks.named('processContractTestResources') {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Important: Run publishStubsPublicationToLocalRepository task directly (not build)
// Contract tests are kept for future use when provider services are migrated
```

### MessagingBase.java for Contract Projects

```java
// ftgo-kitchen-service-contracts/src/contractTest/java/.../MessagingBase.java

package net.chrisrichardson.ftgo.kitchenservice.contracts;

import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {MessagingBase.TestConfig.class})
public abstract class MessagingBase {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfig { }

    @Autowired
    private MessageProducer messageProducer;

    // Reply triggers - called by contract tests
    protected void ticketCreated() {
        messageProducer.send("createTicketReply",
                withSuccess(new CreateTicketReply(99L)));
    }

    protected void success() {
        messageProducer.send("kitchenServiceReply", withSuccess());
    }

    // Command triggers - throw UnsupportedOperationException
    // (commands are verified by consumer-side tests)
    protected void createTicket() {
        throw new UnsupportedOperationException();
    }

    protected void confirmCreateTicket() {
        throw new UnsupportedOperationException();
    }
}
```

### Consumer-Side ReplyHandlersTest

```java
// order-service-proxies-kitchen-service/src/contractTest/java/.../ReplyHandlersTest.java

package net.chrisrichardson.ftgo.orderservice.proxies.kitchen;

import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
    ids = "net.chrisrichardson.ftgo:ftgo-kitchen-service-contracts:+",
    stubsMode = StubRunnerProperties.StubsMode.REMOTE,
    stubsPerConsumer = true,
    consumerName = "order-service"
)
@DirtiesContext
public class ReplyHandlersTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfiguration {
        @Bean
        public TestReplyConsumer testReplyConsumer(MessageConsumer messageConsumer) {
            return new TestReplyConsumer("replyHandlerTest", "createTicketReply", messageConsumer);
        }
    }

    @Autowired
    private StubFinder stubFinder;

    @Autowired
    private TestReplyConsumer testReplyConsumer;

    @Test
    public void shouldHandleTicketCreatedReply() {
        stubFinder.trigger("ticketCreated");
        testReplyConsumer.assertReplyOfTypeReceived(CreateTicketReply.class);
    }
}
```

---

## Migration Path

When a provider service is migrated to multi-module structure:

1. Move contract definitions from `ftgo-XYZ-service-contracts/` into `ftgo-XYZ-service/XYZ-service-api/src/contractTest/`
2. Move API classes if not already in `XYZ-service-api`
3. Update `build.gradle` in the API module to include contract publishing
4. Delete the standalone `ftgo-XYZ-service-contracts/` project
5. Update consumer dependencies to use new artifact ID

---

## Verification Checklist

After implementation, verify:

**Provider Contract Projects (publish only, tests disabled for now):**
- [x] `(cd ftgo-kitchen-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)` succeeds
- [x] `(cd ftgo-accounting-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)` succeeds
- [x] `(cd ftgo-consumer-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)` succeeds
- [x] `(cd ftgo-restaurant-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)` succeeds
- [x] `(cd ftgo-order-service-contracts && ./gradlew publishStubsPublicationToLocalRepository)` succeeds
- [x] Stubs are published to `build/repo/` directory

**Consumer Tests in ftgo-order-service (using regular test source set):**
- [x] `./gradlew :order-service-proxies-kitchen-service:test --tests KitchenServiceContractTest` passes
- [x] `./gradlew :order-service-proxies-accounting-service:test --tests AccountingServiceContractTest` passes
- [x] `./gradlew :order-service-proxies-consumer-service:test --tests ConsumerServiceContractTest` passes
- [x] `./gradlew :order-service-event-handling:test --tests RestaurantServiceContractTest` passes
- [x] Consumer tests can load stubs via `@AutoConfigureStubRunner(stubsMode = REMOTE)`

**Integration:**
- [x] `build-and-test-all.sh` completes successfully

**Note:** Contract tests in provider projects (MessagingBase.java, etc.) are kept but disabled. They will be enabled when each provider service is migrated to the multi-module structure.

---

## References

- [Spring Cloud Contract Documentation](https://docs.spring.io/spring-cloud-contract/docs/current/reference/html/)
- eventuate-examples-realguardio: Domain event contract patterns
- eventuate-tram-sagas-examples-customers-and-orders: Saga command/reply patterns
- eventuate-tram-spring-testing-support-cloud-contract: Framework support classes
