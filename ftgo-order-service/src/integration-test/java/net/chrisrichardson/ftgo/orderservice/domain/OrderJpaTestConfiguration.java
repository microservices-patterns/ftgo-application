package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import io.eventuate.util.spring.swagger.CommonSwaggerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration(exclude = {TramConsumerJdbcAutoConfiguration.class, CommonSwaggerConfiguration.class})
public class OrderJpaTestConfiguration {
}
