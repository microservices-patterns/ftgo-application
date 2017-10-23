package net.chrisrichardson.ftgo.consumerservice.web;

import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(ConsumerServiceConfiguration.class)
public class ConsumerWebConfiguration {
}
