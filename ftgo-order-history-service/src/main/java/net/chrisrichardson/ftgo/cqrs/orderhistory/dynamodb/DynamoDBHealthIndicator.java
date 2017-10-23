package net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class DynamoDBHealthIndicator implements HealthIndicator {
  private final Table table;
  private DynamoDB dynamoDB;

  public DynamoDBHealthIndicator(DynamoDB dynamoDB) {
    this.dynamoDB = dynamoDB;
    this.table = this.dynamoDB.getTable(OrderHistoryDaoDynamoDb.FTGO_ORDER_HISTORY_BY_ID);
  }

  @Override
  public Health health() {
    try {
      table.getItem(OrderHistoryDaoDynamoDb.makePrimaryKey("999"));
      return Health.up().build();
    } catch (Exception e) {
      return Health.down(e).build();
    }
  }
}
