package net.chrisrichardson.ftgo.kitchenservice.main;

import net.chrisrichardson.ftgo.kitchenservice.web.KitchenServiceWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({KitchenServiceWebConfiguration.class,
        KitchenServiceMessageHandlersConfiguration.class})
public class KitchenServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(KitchenServiceMain.class, args);
  }
}
