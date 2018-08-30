import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.lang.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InitDynamoDb extends DefaultTask {

  private static final String TABLE_NAME = "ftgo-order-history";

  private static final String SECONDARY_INDEX_NAME = "ftgo-order-history-by-consumer-id-and-creation-time";

  private static final String CONSUMER_ID_FIELD_NAME = "consumerId";
  private static final String ORDER_ID_FIELD_NAME = "orderId";
  private static final String CREATION_DATE_FIELD_NAME = "creationDate";

  private static final long READ_CAPACITY_UNITS = 3;
  private static final long WRITE_CAPACITY_UNITS = 3;

  @TaskAction
  public void initDynamoDb() {
    System.out.println("Initializing dynamodb...");

    DynamoDB dynamoDB = createDynamoDb(createAmazonDynamoDbClient());

    Table table = dynamoDB.getTable(TABLE_NAME);

    System.out.println("Checking if table already exists...");

    try {
      table.describe();
      System.out.println("Table already exists, do nothing");
    } catch (ResourceNotFoundException e) {
      try {
        System.out.println("Table doesn't exist, creating...");
        createTable(dynamoDB);
        System.out.println("Table created");
      } catch (InterruptedException ie) {
        System.out.println("Table creation failed");
        throw new RuntimeException(ie);
      }
    } finally {
      dynamoDB.shutdown();
    }
  }

  private void createTable(DynamoDB dynamoDB) throws InterruptedException {

    List<KeySchemaElement> keySchema =
            Collections.singletonList(new KeySchemaElement().withAttributeName(ORDER_ID_FIELD_NAME).withKeyType(KeyType.HASH));

    List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

    attributeDefinitions
            .add(new AttributeDefinition().withAttributeName(ORDER_ID_FIELD_NAME).withAttributeType("S"));
    attributeDefinitions
            .add(new AttributeDefinition().withAttributeName(CONSUMER_ID_FIELD_NAME).withAttributeType("S"));
    attributeDefinitions
            .add(new AttributeDefinition().withAttributeName(CREATION_DATE_FIELD_NAME).withAttributeType("N"));

    List<GlobalSecondaryIndex> globalSecondaryIndexes =
            Collections.singletonList(new GlobalSecondaryIndex()
                    .withIndexName(SECONDARY_INDEX_NAME)
                    .withKeySchema(new KeySchemaElement().withAttributeName(CONSUMER_ID_FIELD_NAME).withKeyType(KeyType.HASH),
                            new KeySchemaElement().withAttributeName(CREATION_DATE_FIELD_NAME).withKeyType(KeyType.RANGE))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(READ_CAPACITY_UNITS)
                            .withWriteCapacityUnits(WRITE_CAPACITY_UNITS)));

    CreateTableRequest request =
            new CreateTableRequest()
                    .withTableName(TABLE_NAME)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(READ_CAPACITY_UNITS)
                            .withWriteCapacityUnits(WRITE_CAPACITY_UNITS));

    request.setGlobalSecondaryIndexes(globalSecondaryIndexes);
    request.setAttributeDefinitions(attributeDefinitions);

    Table table = dynamoDB.createTable(request);

    table.waitForActive();
  }

  private DynamoDB createDynamoDb(AmazonDynamoDB client) {
    return new DynamoDB(client);
  }


  private AmazonDynamoDB createAmazonDynamoDbClient() {
    String awsDynamodbEndpointUrl = System.getenv("AWS_DYNAMODB_ENDPOINT_URL");
    String awsRegion = System.getenv("AWS_REGION");

    if (!StringUtils.isBlank(awsDynamodbEndpointUrl)) {
      return AmazonDynamoDBClientBuilder
              .standard()
              .withEndpointConfiguration(
                      new AwsClientBuilder.EndpointConfiguration(awsDynamodbEndpointUrl, awsRegion))
              .build();
    } else {
      return AmazonDynamoDBClientBuilder
              .standard()
              .withRegion(awsRegion)
              .withCredentials(new EnvironmentVariableCredentialsProvider())
              .build();
    }
  }
}