package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.orderservice.persistence.OrderPersistenceConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({OrderServiceConfiguration.class, OrderPersistenceConfiguration.class})
public class OrderServiceWithRepositoriesConfiguration {


}
