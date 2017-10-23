package net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryDao;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderHistoryDynamoDBConfiguration {

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-1")
            .withCredentials(new EnvironmentVariableCredentialsProvider())
            .build();
  }

  @Bean
  public DynamoDB dynamoDB(AmazonDynamoDB client) {
    return   new DynamoDB(client);
  }

  @Bean
  public OrderHistoryDao orderHistoryDao(AmazonDynamoDB client, DynamoDB dynamoDB) {
    return new OrderHistoryDaoDynamoDb(dynamoDB);
  }

  @Bean
  public HealthIndicator dynamoDBHealthIndicator(DynamoDB dynamoDB) {
    return new DynamoDBHealthIndicator(dynamoDB);
  }
}
