package net.chrisrichardson.ftgo.cqrs.orderhistory.web;

import net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb.OrderHistoryDynamoDBConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(OrderHistoryDynamoDBConfiguration.class)
public class OrderHistoryWebConfiguration {
}
