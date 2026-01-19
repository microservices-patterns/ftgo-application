package net.chrisrichardson.ftgo.consumerservice.persistence;

import net.chrisrichardson.ftgo.consumerservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = ConsumerRepository.class)
@EntityScan(basePackageClasses = Consumer.class)
public class ConsumerPersistenceConfiguration {
}
