package net.chrisrichardson.ftgo.deliveryservice.web;

import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryServiceDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({DeliveryServiceDomainConfiguration.class})
public class DeliveryServiceWebConfiguration {
}
