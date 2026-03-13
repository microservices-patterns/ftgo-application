package net.chrisrichardson.ftgo.endtoendtests;

import io.eventuate.cdc.testcontainers.EventuateCdcContainer;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.EventuateVanillaPostgresContainer;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;

import java.nio.file.Paths;

/**
 * Implementation that programmatically starts all FTGO services using Testcontainers.
 * This provides full isolation and doesn't require pre-started services.
 */
public class ApplicationUnderTestUsingTestContainers implements ApplicationUnderTest {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUnderTestUsingTestContainers.class);
    private static final String NETWORK_NAME = "ftgo-e2e-test";
    private static final String BASE_IMAGE_VERSION = "0.1.0.RELEASE";

    private final EventuateKafkaNativeCluster eventuateKafkaCluster;
    private final EventuateKafkaNativeContainer kafka;

    // Databases
    private final EventuateDatabaseContainer<?> consumerDatabase;
    private final EventuateDatabaseContainer<?> orderDatabase;
    private final EventuateDatabaseContainer<?> kitchenDatabase;
    private final EventuateDatabaseContainer<?> restaurantDatabase;
    private final EventuateDatabaseContainer<?> accountingDatabase;
    private final EventuateDatabaseContainer<?> deliveryDatabase;

    // DynamoDB for Order History
    private final GenericContainer<?> dynamoDb;

    // Services
    private final GenericContainer<?> consumerService;
    private final GenericContainer<?> orderService;
    private final GenericContainer<?> kitchenService;
    private final GenericContainer<?> restaurantService;
    private final GenericContainer<?> accountingService;
    private final GenericContainer<?> deliveryService;
    private final GenericContainer<?> orderHistoryService;
    private final GenericContainer<?> apiGateway;

    // CDC
    private final EventuateCdcContainer cdc;

    public ApplicationUnderTestUsingTestContainers() {
        eventuateKafkaCluster = new EventuateKafkaNativeCluster(NETWORK_NAME);

        kafka = eventuateKafkaCluster.kafka
                .withNetworkAliases("kafka")
                .withReuse(true);

        // Create databases for each service
        consumerDatabase = createDatabase("consumer-service-db");
        orderDatabase = createDatabase("order-service-db");
        kitchenDatabase = createDatabase("kitchen-service-db");
        restaurantDatabase = createDatabase("restaurant-service-db");
        accountingDatabase = createDatabase("accounting-service-db");
        deliveryDatabase = createDatabase("delivery-service-db");

        // DynamoDB Local for Order History
        dynamoDb = new GenericContainer<>("amazon/dynamodb-local:latest")
                .withNetwork(eventuateKafkaCluster.network)
                .withNetworkAliases("dynamodb")
                .withExposedPorts(8000)
                .withCommand("-jar DynamoDBLocal.jar -sharedDb")
                .withReuse(true);

        // Create service containers
        consumerService = createServiceContainer("ftgo-consumer-service", "consumer-service-main", consumerDatabase, 8080);
        orderService = createServiceContainer("ftgo-order-service", "order-service-main", orderDatabase, 8080);
        kitchenService = createServiceContainer("ftgo-kitchen-service", "kitchen-service-main", kitchenDatabase, 8080);
        restaurantService = createServiceContainer("ftgo-restaurant-service", "restaurant-service-main", restaurantDatabase, 8080);
        accountingService = createServiceContainer("ftgo-accounting-service", "accounting-service-main", accountingDatabase, 8080);
        deliveryService = createServiceContainer("ftgo-delivery-service", "delivery-service-main", deliveryDatabase, 8080);

        // Order History Service needs DynamoDB
        orderHistoryService = createOrderHistoryServiceContainer();

        // API Gateway
        apiGateway = createApiGatewayContainer();

        // CDC service
        cdc = new EventuateCdcContainer()
                .withKafka(kafka)
                .withKafkaLeadership()
                .withTramPipeline(consumerDatabase)
                .withTramPipeline(orderDatabase)
                .withTramPipeline(kitchenDatabase)
                .withTramPipeline(restaurantDatabase)
                .withTramAndLocalPipeline(accountingDatabase)
                .withTramPipeline(deliveryDatabase)
                .withReuse(false)
                .withExposedPorts(8080)
                .dependsOn(consumerService, orderService, kitchenService, restaurantService, accountingService, deliveryService)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC cdc"));
    }

    private EventuateDatabaseContainer<?> createDatabase(String alias) {
        return new EventuateVanillaPostgresContainer()
                .withNetwork(eventuateKafkaCluster.network)
                .withNetworkAliases(alias)
                .withReuse(true);
    }

    private GenericContainer<?> createServiceContainer(String serviceDir, String mainModule, EventuateDatabaseContainer<?> database, int port) {
        return new ServiceContainer(new ImageFromDockerfile()
                .withFileFromPath(".", Paths.get("..", serviceDir, mainModule).toAbsolutePath())
                .withDockerfilePath("Dockerfile")
                .withBuildArg("baseImageVersion", BASE_IMAGE_VERSION))
                .withNetwork(eventuateKafkaCluster.network)
                .withNetworkAliases(serviceDir)
                .withDatabase(database)
                .withKafka(kafka)
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withEnv("SPRING_JPA_HIBERNATE_DDL_AUTO", "update")
                .withExposedPorts(port)
                .withReuse(false)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC " + serviceDir));
    }

    private GenericContainer<?> createOrderHistoryServiceContainer() {
        return new ServiceContainer(new ImageFromDockerfile()
                .withFileFromPath(".", Paths.get("..", "ftgo-order-history-service", "order-history-service-main").toAbsolutePath())
                .withDockerfilePath("Dockerfile")
                .withBuildArg("baseImageVersion", BASE_IMAGE_VERSION))
                .withNetwork(eventuateKafkaCluster.network)
                .withNetworkAliases("ftgo-order-history-service")
                .withKafka(kafka)
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withEnv("AWS_DYNAMODB_ENDPOINT_URL", "http://dynamodb:8000")
                .withEnv("AWS_ACCESS_KEY_ID", "test")
                .withEnv("AWS_SECRET_ACCESS_KEY", "test")
                .withEnv("AWS_REGION", "us-east-1")
                .withExposedPorts(8080)
                .withReuse(false)
                .dependsOn(dynamoDb)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC order-history"));
    }

    private GenericContainer<?> createApiGatewayContainer() {
        return new ServiceContainer(new ImageFromDockerfile()
                .withFileFromPath(".", Paths.get("..", "ftgo-api-gateway", "api-gateway-main").toAbsolutePath())
                .withDockerfilePath("Dockerfile")
                .withBuildArg("baseImageVersion", BASE_IMAGE_VERSION))
                .withNetwork(eventuateKafkaCluster.network)
                .withNetworkAliases("ftgo-api-gateway")
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withEnv("ORDER_DESTINATIONS_ORDERSERVICEURL", "http://ftgo-order-service:8080")
                .withEnv("ORDER_DESTINATIONS_ORDERHISTORYSERVICEURL", "http://ftgo-order-history-service:8080")
                .withEnv("CONSUMER_DESTINATIONS_CONSUMERSERVICEURL", "http://ftgo-consumer-service:8080")
                .withExposedPorts(8080)
                .withReuse(false)
                .dependsOn(consumerService, orderService, orderHistoryService)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC api-gateway"));
    }

    @Override
    public void start() {
        kafka.withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC kafka"));
        Startables.deepStart(
                kafka,
                dynamoDb,
                consumerService,
                orderService,
                kitchenService,
                restaurantService,
                accountingService,
                deliveryService,
                orderHistoryService,
                apiGateway,
                cdc
        ).join();
    }

    @Override
    public void stop() {
        // Testcontainers manages cleanup automatically
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public int getConsumerServicePort() {
        return consumerService.getFirstMappedPort();
    }

    @Override
    public int getOrderServicePort() {
        return orderService.getFirstMappedPort();
    }

    @Override
    public int getKitchenServicePort() {
        return kitchenService.getFirstMappedPort();
    }

    @Override
    public int getRestaurantServicePort() {
        return restaurantService.getFirstMappedPort();
    }

    @Override
    public int getAccountingServicePort() {
        return accountingService.getFirstMappedPort();
    }

    @Override
    public int getOrderHistoryServicePort() {
        return orderHistoryService.getFirstMappedPort();
    }

    @Override
    public int getApiGatewayPort() {
        return apiGateway.getFirstMappedPort();
    }

    @Override
    public int getDeliveryServicePort() {
        return deliveryService.getFirstMappedPort();
    }
}
