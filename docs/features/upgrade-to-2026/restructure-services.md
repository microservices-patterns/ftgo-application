# Restructure Services: Use Eventuate Starters for Auto-Configuration

This plan applies the patterns established in ftgo-order-service to other services.

## Goal

Replace explicit Eventuate configuration imports with Spring Boot starters that provide auto-configuration, following the example set by `eventuate-tram-sagas-examples-customers-and-orders`.

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

## Services to Update

### 1. ftgo-consumer-service - [x] DONE

**Files to modify:**

- `consumer-service-command-handlers/build.gradle`:
  - Change `eventuate-tram-sagas-spring-participant` → `eventuate-tram-sagas-spring-participant-starter`

- `consumer-service-main/build.gradle`:
  - Remove redundant `eventuate-tram-sagas-spring-participant` (comes transitively)
  - Remove redundant `eventuate-tram-spring-events-publisher-starter` if in event-publishing module

- `consumer-service-main/.../ConsumerServiceConfiguration.java`:
  - Remove `TramEventsPublisherConfiguration` import
  - Remove `SagaParticipantConfiguration` import

- `consumer-service-main/.../ConsumerServiceMain.java`:
  - Remove `TramJdbcKafkaConfiguration` import

### 2. ftgo-kitchen-service - [x] DONE

**Already using starters** - verify and clean up if needed:

- `kitchen-service-main/.../KitchenServiceMain.java`:
  - Remove `TramJdbcKafkaConfiguration` import

- `kitchen-service-main/.../KitchenServiceMessageHandlersConfiguration.java`:
  - Remove `TramEventSubscriberConfiguration` import (has subscriber-starter)

### 3. ftgo-accounting-service - [ ]

**Files to modify:**

- `accounting-service-command-handlers/build.gradle`:
  - Change `eventuate-tram-sagas-spring-participant` → `eventuate-tram-sagas-spring-participant-starter`

- `accounting-service-main/.../AccountingMessagingConfiguration.java`:
  - Remove `TramEventSubscriberConfiguration` import (has subscriber-starter)
  - Remove `TramCommandConsumerConfiguration` if auto-configured

- `accounting-service-main/.../AccountingServiceMain.java`:
  - Remove `TramJdbcKafkaConfiguration` import

- `accounting-service-domain/.../AccountServiceConfiguration.java`:
  - Review `TramCommandProducerConfiguration` import

### 4. ftgo-delivery-service - [ ]

**Files to modify:**

- `delivery-service-event-handling/.../DeliveryServiceMessagingConfiguration.java`:
  - Remove `TramEventSubscriberConfiguration` import (has subscriber-starter)

- `delivery-service-main/.../DeliveryServiceMain.java`:
  - Remove `TramJdbcKafkaConfiguration` import

### 5. ftgo-restaurant-service - [ ]

**Files to modify:**

- `restaurant-service-main/.../RestaurantServiceMain.java`:
  - Remove `TramJdbcKafkaConfiguration` import

- `restaurant-service-domain/.../RestaurantServiceDomainConfiguration.java`:
  - Remove `TramEventsPublisherConfiguration` import (has publisher-starter)

### 6. ftgo-order-history-service - [ ]

**Files to modify:**

- `order-history-service-main/.../OrderHistoryServiceMain.java`:
  - Review consumer configuration imports

## Verification

After each service change:
1. Run `./gradlew build` in the service directory
2. Run `./build-and-test-all.sh` from project root
3. Commit changes

## Implementation Order

1. ftgo-kitchen-service (already mostly done, quick cleanup)
2. ftgo-consumer-service
3. ftgo-accounting-service
4. ftgo-delivery-service
5. ftgo-restaurant-service
6. ftgo-order-history-service
