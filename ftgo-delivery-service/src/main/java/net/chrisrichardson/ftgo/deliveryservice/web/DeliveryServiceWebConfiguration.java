package net.chrisrichardson.ftgo.deliveryservice.web;

import net.chrisrichardson.ftgo.common.CommonConfiguration;
import net.chrisrichardson.ftgo.deliveryservice.domain.DeliveryServiceDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({DeliveryServiceDomainConfiguration.class, CommonConfiguration.class})
public class DeliveryServiceWebConfiguration {
}
