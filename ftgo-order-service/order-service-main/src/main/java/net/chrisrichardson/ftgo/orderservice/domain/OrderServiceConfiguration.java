package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.spring.flyway.EventuateTramFlywayMigrationConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.orderservice.persistence.OrderPersistenceConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.AccountingServiceProxyConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConsumerServiceProxyConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.KitchenServiceProxyConfiguration;
import net.chrisrichardson.ftgo.orderservice.sagas.OrderSagasConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CommonConfiguration.class,
        EventuateTramFlywayMigrationConfiguration.class,
        OrderPersistenceConfiguration.class,
        OrderDomainConfiguration.class,
        OrderSagasConfiguration.class,
        KitchenServiceProxyConfiguration.class,
        ConsumerServiceProxyConfiguration.class,
        AccountingServiceProxyConfiguration.class
})
public class OrderServiceConfiguration {

  @Bean
  @ConditionalOnBean(MeterRegistry.class)
  public OrderServiceInstrumentation micrometerOrderServiceInstrumentation(MeterRegistry meterRegistry) {
    return new MicrometerOrderServiceInstrumentation(meterRegistry);
  }

  @Bean
  @ConditionalOnMissingBean(OrderServiceInstrumentation.class)
  public OrderServiceInstrumentation noOpOrderServiceInstrumentation() {
    return new NoOpOrderServiceInstrumentation();
  }

  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
    return registry -> registry.config().commonTags("service", serviceName);
  }
}
