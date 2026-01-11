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

- [ ] **1.1.1** Reorganize contracts into per-consumer structure
- [ ] **1.1.2** Update contract syntax from `messageFrom` to `triggeredBy` pattern
- [ ] **1.1.3** Update `build.gradle` with modern configuration
- [ ] **1.1.4** Add `MessagingBase.java` for contract verification
- [ ] **1.1.5** Enable `generateContractTests`

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

- [ ] **1.2.1** Reorganize contracts into per-consumer structure
- [ ] **1.2.2** Update contract syntax to `triggeredBy` pattern
- [ ] **1.2.3** Update `build.gradle`
- [ ] **1.2.4** Add `MessagingBase.java`

**Current:** `contracts/Authorize.groovy`
**Target:** `contracts/order-service/commands/authorizeCommand.groovy`

#### 1.3 ftgo-consumer-service-contracts

- [ ] **1.3.1** Reorganize contracts into per-consumer structure
- [ ] **1.3.2** Update contract syntax
- [ ] **1.3.3** Update `build.gradle`
- [ ] **1.3.4** Add `MessagingBase.java`

**Current:** `contracts/VerifyConsumer.groovy`
**Target:** `contracts/order-service/commands/validateOrderByConsumerCommand.groovy`

#### 1.4 ftgo-restaurant-service-contracts

- [ ] **1.4.1** Reorganize contracts into per-consumer structure
- [ ] **1.4.2** Update contract syntax
- [ ] **1.4.3** Update `build.gradle`
- [ ] **1.4.4** Add `MessagingBase.java`

**Current:** `contracts/deliveryservice/messaging/RestaurantCreatedEvent.groovy`
**Target:** `contracts/event-consumer/order-service/restaurantCreatedEvent.groovy`

### Phase 2: Update build-and-test-all.sh

- [ ] **2.1** Add contract publishing step before service tests
- [ ] **2.2** Ensure proper ordering of build tasks

**Add to build-and-test-all.sh:**
```bash
# Publish all contract stubs first
echo "Publishing contract stubs..."
./gradlew :ftgo-kitchen-service-contracts:publishStubsPublicationToStubsRepository
./gradlew :ftgo-accounting-service-contracts:publishStubsPublicationToStubsRepository
./gradlew :ftgo-consumer-service-contracts:publishStubsPublicationToStubsRepository
./gradlew :ftgo-restaurant-service-contracts:publishStubsPublicationToStubsRepository
```

### Phase 3: Update Consumer-Side Tests in ftgo-order-service

#### 3.1 order-service-proxies-kitchen-service

- [ ] **3.1.1** Create `src/contractTest/` directory structure
- [ ] **3.1.2** Create `BaseForKitchenServiceTest.java`
- [ ] **3.1.3** Create `ReplyHandlersTest.java` using `StubFinder.trigger()`
- [ ] **3.1.4** Update `build.gradle` with contract test configuration

#### 3.2 order-service-proxies-accounting-service

- [ ] **3.2.1** Create `src/contractTest/` directory structure
- [ ] **3.2.2** Create `BaseForAccountingServiceTest.java`
- [ ] **3.2.3** Create `ReplyHandlersTest.java`
- [ ] **3.2.4** Update `build.gradle`

#### 3.3 order-service-proxies-consumer-service

- [ ] **3.3.1** Create `src/contractTest/` directory structure
- [ ] **3.3.2** Create `BaseForConsumerServiceTest.java`
- [ ] **3.3.3** Create `ReplyHandlersTest.java`
- [ ] **3.3.4** Update `build.gradle`

#### 3.4 order-service-event-handling

- [ ] **3.4.1** Update existing `MessagingBase.java` to use `@EnableEventuateTramContractVerifier`
- [ ] **3.4.2** Add consumer contract tests for restaurant events using `StubFinder`
- [ ] **3.4.3** Update `build.gradle`

### Phase 4: Update ftgo-order-service-contracts (Provider)

- [ ] **4.1** Reorganize contracts into per-consumer structure
- [ ] **4.2** Update contract syntax to `triggeredBy` pattern
- [ ] **4.3** Update existing `MessagingBase.java` to support contract triggers
- [ ] **4.4** Update `build.gradle` with proper stub publishing

**Current Structure:**
```
contracts/
├── deliveryservice/messaging/OrderCreatedEvent.groovy
├── http/GetOrder.groovy
├── http/GetNonExistentOrder.groovy
└── messaging/OrderCreatedEvent.groovy
```

**Target Structure:**
```
contracts/
├── event-consumer/
│   └── delivery-service/
│       └── orderCreatedEvent.groovy
└── http/
    ├── getOrder.groovy
    └── getNonExistentOrder.groovy
```

### Phase 5: Build Integration

- [ ] **5.1** Configure stub repository URL in `gradle.properties`
- [ ] **5.2** Update GitHub Actions workflow to publish contracts before tests
- [ ] **5.3** Ensure stub publishing happens before consumer tests

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
}

// Remove: generateContractTests.enabled = false
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

- [ ] `./gradlew :ftgo-kitchen-service-contracts:contractTest` passes
- [ ] `./gradlew :ftgo-kitchen-service-contracts:publishStubsPublicationToStubsRepository` succeeds
- [ ] `./gradlew :ftgo-accounting-service-contracts:contractTest` passes
- [ ] `./gradlew :ftgo-consumer-service-contracts:contractTest` passes
- [ ] `./gradlew :ftgo-restaurant-service-contracts:contractTest` passes
- [ ] `./gradlew :ftgo-order-service:order-service-proxies-kitchen-service:contractTest` passes
- [ ] `./gradlew :ftgo-order-service:order-service-proxies-accounting-service:contractTest` passes
- [ ] `./gradlew :ftgo-order-service:order-service-proxies-consumer-service:contractTest` passes
- [ ] `./gradlew :ftgo-order-service:order-service-event-handling:contractTest` passes
- [ ] Consumer tests can load stubs via `@AutoConfigureStubRunner`
- [ ] `build-and-test-all.sh` completes successfully

---

## References

- [Spring Cloud Contract Documentation](https://docs.spring.io/spring-cloud-contract/docs/current/reference/html/)
- eventuate-examples-realguardio: Domain event contract patterns
- eventuate-tram-sagas-examples-customers-and-orders: Saga command/reply patterns
- eventuate-tram-spring-testing-support-cloud-contract: Framework support classes
