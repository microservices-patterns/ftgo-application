package net.chrisrichardson.ftgo.cqrs.orderhistory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeCluster;
import io.eventuate.messaging.kafka.testcontainers.EventuateKafkaNativeContainer;
import io.eventuate.testcontainers.service.ServiceContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

public class OrderHistoryServiceOutOfProcessComponentTest {

    protected static Logger logger = LoggerFactory.getLogger(OrderHistoryServiceOutOfProcessComponentTest.class);
    private String baseUri;

    public static EventuateKafkaNativeCluster eventuateKafkaCluster = new EventuateKafkaNativeCluster("order-history-service-oop-tests");

    public static EventuateKafkaNativeContainer kafka = eventuateKafkaCluster.kafka
            .withNetworkAliases("kafka")
            .withReuse(false);

    public static LocalStackContainer dynamodb = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(DYNAMODB)
            .withNetwork(eventuateKafkaCluster.network)
            .withNetworkAliases("dynamodb")
            .withReuse(false);

    public static GenericContainer<?> service =
            new ServiceContainer(new ImageFromDockerfile()
                    .withFileFromPath(".", Paths.get(".").toAbsolutePath())
                    .withDockerfilePath("Dockerfile")
                    .withBuildArg("baseImageVersion", "BUILD-15"))
                    .withNetwork(eventuateKafkaCluster.network)
                    .withKafka(kafka)
                    .withEnv("AWS_DYNAMODB_ENDPOINT_URL", "http://dynamodb:4566")
                    .withEnv("AWS_ACCESS_KEY_ID", "test")
                    .withEnv("AWS_SECRET_ACCESS_KEY", "test")
                    .withEnv("AWS_REGION", "us-east-1")
                    .withReuse(false)
                    .dependsOn(dynamodb)
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC order-history-service:"));

    @BeforeAll
    static void startContainers() {
        // Start infrastructure first
        Startables.deepStart(kafka, dynamodb).join();

        // Create DynamoDB table before starting the service
        createDynamoDBTable();

        // Now start the service
        service.start();
    }

    private static void createDynamoDBTable() {
        String endpoint = dynamodb.getEndpointOverride(DYNAMODB).toString();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, dynamodb.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(dynamodb.getAccessKey(), dynamodb.getSecretKey())))
                .build();

        CreateTableRequest request = new CreateTableRequest()
                .withTableName("ftgo-order-history")
                .withKeySchema(new KeySchemaElement("orderId", KeyType.HASH))
                .withAttributeDefinitions(Arrays.asList(
                        new AttributeDefinition("orderId", ScalarAttributeType.S),
                        new AttributeDefinition("consumerId", ScalarAttributeType.S),
                        new AttributeDefinition("creationDate", ScalarAttributeType.N)
                ))
                .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
                        .withIndexName("ftgo-order-history-by-consumer-id-and-creation-time")
                        .withKeySchema(Arrays.asList(
                                new KeySchemaElement("consumerId", KeyType.HASH),
                                new KeySchemaElement("creationDate", KeyType.RANGE)
                        ))
                        .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                        .withProvisionedThroughput(new ProvisionedThroughput(3L, 3L))
                )
                .withProvisionedThroughput(new ProvisionedThroughput(3L, 3L));

        client.createTable(request);
        logger.info("Created DynamoDB table: ftgo-order-history");
    }

    @BeforeEach
    void setup() {
        baseUri = String.format("http://localhost:%d", service.getFirstMappedPort());
    }

    @Test
    void shouldStart() {
        assertThat(service.isRunning()).isTrue();
        assertThat(service.getFirstMappedPort()).isNotNull();
    }

    @Test
    void healthEndpointReturnsOk() {
        RestAssured.given()
                .baseUri(baseUri)
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString()
                .contains("UP");
    }

    @Test
    void shouldReturnEmptyOrdersForNonExistentConsumer() {
        RestAssured.given()
                .baseUri(baseUri)
                .when()
                .get("/orders?consumerId=999999")
                .then()
                .statusCode(200);
    }
}
