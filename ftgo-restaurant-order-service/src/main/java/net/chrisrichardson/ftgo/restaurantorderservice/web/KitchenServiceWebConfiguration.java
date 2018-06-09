package net.chrisrichardson.ftgo.restaurantorderservice.web;

import net.chrisrichardson.ftgo.restaurantorderservice.domain.KitchenDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KitchenDomainConfiguration.class)
@ComponentScan
public class KitchenServiceWebConfiguration {


}
