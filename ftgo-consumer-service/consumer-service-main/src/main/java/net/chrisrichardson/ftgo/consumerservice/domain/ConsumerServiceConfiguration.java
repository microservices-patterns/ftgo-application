package net.chrisrichardson.ftgo.consumerservice.domain;

import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.consumerservice.persistence.ConsumerPersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CommonConfiguration.class,
        EventuateTramFlywayMigrationConfiguration.class,
        ConsumerPersistenceConfiguration.class,
        ConsumerCommandHandlersConfiguration.class
})
public class ConsumerServiceConfiguration {
}
