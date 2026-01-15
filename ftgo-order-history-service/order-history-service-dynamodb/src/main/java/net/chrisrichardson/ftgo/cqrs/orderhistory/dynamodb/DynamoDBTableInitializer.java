package net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class DynamoDBTableInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBTableInitializer.class);
    private static final String TABLE_NAME = "ftgo-order-history";
    private static final String GSI_NAME = "ftgo-order-history-by-consumer-id-and-creation-time";

    private final AmazonDynamoDB amazonDynamoDB;

    public DynamoDBTableInitializer(AmazonDynamoDB amazonDynamoDB) {
        this.amazonDynamoDB = amazonDynamoDB;
    }

    @PostConstruct
    public void createTableIfNotExists() {
        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(TABLE_NAME)
                    .withKeySchema(new KeySchemaElement("orderId", KeyType.HASH))
                    .withAttributeDefinitions(Arrays.asList(
                            new AttributeDefinition("orderId", ScalarAttributeType.S),
                            new AttributeDefinition("consumerId", ScalarAttributeType.S),
                            new AttributeDefinition("creationDate", ScalarAttributeType.N)
                    ))
                    .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
                            .withIndexName(GSI_NAME)
                            .withKeySchema(Arrays.asList(
                                    new KeySchemaElement("consumerId", KeyType.HASH),
                                    new KeySchemaElement("creationDate", KeyType.RANGE)
                            ))
                            .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                            .withProvisionedThroughput(new ProvisionedThroughput(3L, 3L))
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(3L, 3L));

            amazonDynamoDB.createTable(request);
            logger.info("Created DynamoDB table: {}", TABLE_NAME);
        } catch (ResourceInUseException e) {
            logger.info("DynamoDB table already exists: {}", TABLE_NAME);
        }
    }
}
