package net.chrisrichardson.ftgo.apiagateway;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ApiGatewayIntegrationTestConfiguration {

  // Force it to be Netty to avoid casting exception in NettyWriteResponseFilter
  // Wiremock adds dependency on Jetty

  @Bean
  public NettyReactiveWebServerFactory NettyReactiveWebServerFactory() {
    return new NettyReactiveWebServerFactory();
  }


}
