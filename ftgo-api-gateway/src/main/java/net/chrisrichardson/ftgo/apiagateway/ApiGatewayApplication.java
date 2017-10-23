package net.chrisrichardson.ftgo.apiagateway;


import net.chrisrichardson.ftgo.apiagateway.orders.OrderConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(OrderConfiguration.class)
@ComponentScan
public class ApiGatewayApplication {



  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }
}

