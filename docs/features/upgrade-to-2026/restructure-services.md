# Restructure Services: Apply ftgo-order-service Patterns

This plan applies the patterns established in ftgo-order-service to other services.

## Goals

1. Replace explicit Eventuate configuration imports with Spring Boot starters that provide auto-configuration
2. Move @Configuration classes to the same module as the bean classes they create
3. Reorganize integration tests from *-main to adapter subprojects (Task 9.7)

## Pattern Summary (from ftgo-order-service)

### 1. Use Starters Instead of Non-Starter Dependencies

| Old Dependency | New Starter |
|----------------|-------------|
| `eventuate-tram-sagas-spring-orchestration-simple-dsl` | `eventuate-tram-sagas-spring-orchestration-simple-dsl-starter` |
| `eventuate-tram-sagas-spring-participant` | `eventuate-tram-sagas-spring-participant-starter` |

### 2. Remove Redundant Explicit Configuration Imports

These configurations are auto-configured when using starters:
- `TramEventsPublisherConfiguration` (via events-publisher-starter)
- `TramEventSubscriberConfiguration` (via events-subscriber-starter)
- `SagaOrchestratorConfiguration` (via orchestration-simple-dsl-starter)
- `SagaParticipantConfiguration` (via participant-starter)
- `TramJdbcKafkaConfiguration` (auto-configured via messaging-starter)

### 3. Keep These Explicit Imports

- `EventuateTramFlywayMigrationConfiguration` - no starter, must be explicit
- `CommonConfiguration` - application-specific
- Test-specific configurations (TramInMemoryConfiguration, etc.)

### 4. Move @Configuration Classes to Bean Modules

A @Configuration class that creates beans should be in the same module as the bean classes. This:
- Keeps related code together
- Makes the module self-contained
- Follows the pattern from ftgo-order-service where `OrderDomainConfiguration` is in `order-service-domain`

Example pattern:
- `*ServiceConfiguration` creating domain beans → move to `*-domain` module
- `*SagasConfiguration` creating saga beans → move to `*-sagas` module
- Proxy configurations → move to respective proxy modules

### 5. Reorganize Integration Tests (Task 9.7)

Integration tests should live in the module containing the code they test:

| Test Type | Location |
|-----------|----------|
| Domain unit tests | `*-domain/src/test/` |
| JPA/Repository tests | `*-persistence/src/integrationTest/` |
| Command handler tests | `*-command-handlers/src/test/` |
| Event handler tests | `*-event-handling/src/test/` |
| REST controller tests | `*-restapi/src/test/` |
| Full service integration tests | `*-main/src/integrationTest/` |
| Component tests (out-of-process) | `*-main/src/componentTest/` |

**Principle**: A test should be in the same module as the code it exercises.

## Services to Update

### 1. ftgo-consumer-service

**Starters/Auto-config - [x] DONE:**
- `consumer-service-command-handlers/build.gradle`: Changed to participant-starter
- `consumer-service-main/build.gradle`: Removed redundant dependencies
- `ConsumerServiceConfiguration.java`: Removed redundant Eventuate imports
- `ConsumerServiceMain.java`: Removed `TramJdbcKafkaConfiguration` import

**Move @Configuration classes - [x] DONE:**
- Created `ConsumerDomainConfiguration` in `consumer-service-domain` (creates ConsumerService)
- Created `ConsumerCommandHandlersConfiguration` in `consumer-service-command-handlers` (creates CommandHandlers, CommandDispatcher)
- Created `ConsumerPersistenceConfiguration` in `consumer-service-persistence` (JPA entity/repository scanning)
- Slimmed down `ConsumerServiceConfiguration` in main to import these configurations

**Integration tests - [x] OK:**
- `ConsumerServiceIntegrationTest` in `consumer-service-main/src/integrationTest/` - correctly placed (full service test)

### 2. ftgo-kitchen-service

**Starters/Auto-config - [x] DONE:**
- `KitchenServiceMain.java`: Removed `TramJdbcKafkaConfiguration` import
- `KitchenServiceMessageHandlersConfiguration.java`: Removed `TramEventSubscriberConfiguration` import

**Move @Configuration classes - [x] OK:**
- Configuration classes already properly placed in their respective modules

**Integration tests - [x] OK:**
- Tests correctly placed in their respective modules

### 3. ftgo-accounting-service - [x] DONE (partial)

**Starters/Auto-config - [x] DONE:**
- `accounting-service-command-handlers/build.gradle`: Keep non-starter (starter breaks contract tests due to JDBC auto-config)
- `AccountingMessagingConfiguration.java`: Removed `TramEventSubscriberConfiguration` import
- `AccountingServiceMain.java`: Removed `TramJdbcKafkaConfiguration` import
- `AccountServiceConfiguration.java`: `TramCommandProducerConfiguration` kept (still needed)

**Move @Configuration classes - [ ] TODO:**
- Move `AccountingMessagingConfiguration` from `accounting-service-main/src/main/java/.../messaging/` to `accounting-service-command-handlers/src/main/java/.../messaging/`
  - Creates: `AccountingEventConsumer`, `DomainEventDispatcher`, `AccountingServiceCommandHandler`, `CommandDispatcher`, `SagaReplyRequestedEventSubscriber`
  - Should be with the command/event handlers it configures

**Integration tests - [ ] TODO:**
- Move `AccountingServiceCommandHandlerTest` from `accounting-service-main/src/integrationTest/` to `accounting-service-command-handlers/src/integrationTest/`
  - Tests command handler behavior
  - Should be in the module containing the code it tests

### 4. ftgo-delivery-service - [x] DONE

**Starters/Auto-config - [x] DONE:**
- `DeliveryServiceMessagingConfiguration.java`: Removed `TramEventSubscriberConfiguration` import
- `DeliveryServiceMain.java`: Removed `TramJdbcKafkaConfiguration` import

**Move @Configuration classes - [x] OK:**
- `DeliveryServiceDomainConfiguration` correctly in `delivery-service-domain/`
- `DeliveryServiceMessagingConfiguration` correctly in `delivery-service-event-handling/`
- `DeliveryServiceWebConfiguration` correctly in `delivery-service-restapi/`

**Integration tests - [x] OK:**
- `DeliveryJpaTest`, `RestaurantJpaTest`, `CourierJpaTest` correctly in `delivery-service-persistence/src/integrationTest/`

### 5. ftgo-restaurant-service - [x] DONE

**Starters/Auto-config - [x] DONE:**
- `RestaurantServiceMain.java`: Removed `TramJdbcKafkaConfiguration` import
- `RestaurantServiceDomainConfiguration.java`: No changes needed (no redundant imports)

**Move @Configuration classes - [x] OK:**
- `RestaurantServiceDomainConfiguration` correctly in `restaurant-service-domain/`
- `RestaurantWebConfiguration` correctly in `restaurant-service-restapi/`
- `CommonConfiguration` correctly in `restaurant-service-domain/`

**Integration tests - [x] OK:**
- `RestaurantServiceIntegrationTest` in `restaurant-service-main/src/integrationTest/` - correctly placed (full service test)

### 6. ftgo-order-history-service - [ ]

**Starters/Auto-config - [ ]:**
- `OrderHistoryServiceMain.java`: Review consumer configuration imports

**Move @Configuration classes - [x] OK:**
- `OrderHistoryDynamoDBConfiguration` correctly in `order-history-service-dynamodb/`
- `OrderHistoryWebConfiguration` correctly in `order-history-service-restapi/`
- `OrderHistoryServiceMessagingConfiguration` correctly in `order-history-service-event-handling/`

**Integration tests - [x] OK:**
- `OrderHistoryDaoDynamoDbTest` correctly in `order-history-service-dynamodb/src/integrationTest/`

## Verification

After each service change:
1. Run `./gradlew build` in the service directory
2. Run `./build-and-test-all.sh` from project root
3. Commit changes

## Implementation Order

1. ftgo-kitchen-service - [x] DONE (starters)
2. ftgo-consumer-service - [x] DONE (starters, move config)
3. ftgo-accounting-service - [x] DONE (starters partial - no starter for command-handlers due to contract tests)
4. ftgo-delivery-service - [x] DONE (starters)
5. ftgo-restaurant-service - [x] DONE (starters)
6. ftgo-order-history-service - [ ] (starters only)
