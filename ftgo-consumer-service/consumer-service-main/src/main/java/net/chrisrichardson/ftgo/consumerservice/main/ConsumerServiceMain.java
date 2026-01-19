package net.chrisrichardson.ftgo.consumerservice.main;

import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerServiceConfiguration;
import net.chrisrichardson.ftgo.consumerservice.web.ConsumerWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerServiceConfiguration.class, ConsumerWebConfiguration.class})
public class ConsumerServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(ConsumerServiceMain.class, args);
  }
}
