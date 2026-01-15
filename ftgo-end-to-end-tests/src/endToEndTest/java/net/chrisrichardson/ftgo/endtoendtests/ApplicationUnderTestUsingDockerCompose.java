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

    @Override
    public int getConsumerServicePort() {
        return 8081;
    }

    @Override
    public int getOrderServicePort() {
        return 8082;
    }

    @Override
    public int getKitchenServicePort() {
        return 8083;
    }

    @Override
    public int getRestaurantServicePort() {
        return 8084;
    }

    @Override
    public int getAccountingServicePort() {
        return 8085;
    }

    @Override
    public int getOrderHistoryServicePort() {
        return 8086;
    }

    @Override
    public int getApiGatewayPort() {
        return 8087;
    }

    @Override
    public int getDeliveryServicePort() {
        return 8089;
    }
}
