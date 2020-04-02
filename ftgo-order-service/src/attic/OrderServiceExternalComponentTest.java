package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.spring.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceExternalComponentTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "debug=true")
public class OrderServiceExternalComponentTest extends AbstractOrderServiceComponentTest {


  static {
    CommonJsonMapperInitializer.registerMoneyModule();
  }
  
  private int port = 8082;
  private String host = FtgoTestUtil.getDockerHostIp();

  @Override
  protected String baseUrl(String path) {
    return String.format("http://%s:%s%s", host, port, path);
  }

  @Configuration
  @EnableAutoConfiguration
  @Import({CommonMessagingStubConfiguration.class,
          TramCommandProducerConfiguration.class, TramEventsPublisherConfiguration.class,
          TramJdbcKafkaConfiguration.class})
  public static class TestConfiguration {
  }
}
