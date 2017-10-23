package net.chrisrichardson.ftgo.restaurantorderservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.javaclient.commonimpl.JSonMapper;
import net.chrisrichardson.ftgo.restaurantorderservice.domain.RestaurantOrderDomainConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RestaurantOrderDomainConfiguration.class)
@ComponentScan
public class RestaurantOrderWebConfiguration {

}
