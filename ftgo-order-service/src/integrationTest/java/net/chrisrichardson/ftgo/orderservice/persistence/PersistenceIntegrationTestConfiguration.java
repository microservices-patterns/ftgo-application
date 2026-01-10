package net.chrisrichardson.ftgo.orderservice.persistence;

import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.domain.Restaurant;
import net.chrisrichardson.ftgo.orderservice.domain.RestaurantRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = {OrderRepository.class, RestaurantRepository.class})
@EntityScan(basePackageClasses = {Order.class, Restaurant.class})
public class PersistenceIntegrationTestConfiguration {
}
