package net.chrisrichardson.ftgo.endtoendtests;

/**
 * Implementation that connects to pre-started Docker Compose services.
 * This assumes 'docker compose up' has already been run from the project root.
 */
public class ApplicationUnderTestUsingDockerCompose implements ApplicationUnderTest {

    public ApplicationUnderTestUsingDockerCompose() {
        // No-arg constructor for reflection-based instantiation
    }

    @Override
    public void start() {
        // Services are already running via docker compose
    }

    @Override
    public void stop() {
        // Services are managed externally via docker compose
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    // Port mappings from docker-compose.yaml
    @Override
    public int getConsumerServicePort() {
        return 8082;  // ftgo-consumer-service: 8082:8080
    }

    @Override
    public int getOrderServicePort() {
        return 8081;  // ftgo-order-service: 8081:8080
    }

    @Override
    public int getKitchenServicePort() {
        return 8083;  // ftgo-kitchen-service: 8083:8080
    }

    @Override
    public int getRestaurantServicePort() {
        return 8085;  // ftgo-restaurant-service: 8085:8080
    }

    @Override
    public int getAccountingServicePort() {
        return 8084;  // ftgo-accounting-service: 8084:8080
    }

    @Override
    public int getOrderHistoryServicePort() {
        return 8088;  // ftgo-order-history-service (not yet in docker-compose)
    }

    @Override
    public int getApiGatewayPort() {
        return 8087;  // ftgo-api-gateway (not yet in docker-compose)
    }

    @Override
    public int getDeliveryServicePort() {
        return 8086;  // ftgo-delivery-service: 8086:8080
    }
}
