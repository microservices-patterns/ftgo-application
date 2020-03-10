package net.chrisrichardson.ftgo.orderservice;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceOutOfProcessComponentTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "debug=true")
public class OrderServiceOutOfProcessComponentTest extends AbstractOrderServiceComponentTest {


  @Value("${local.server.port}")
  private int port;

  @Override
  protected String baseUrl(String path) {
    return "http://localhost:" + port + path;
  }

  @Configuration
  @EnableAutoConfiguration
  @Import({CommonTestConfiguration.class, TramJdbcKafkaConfiguration.class})
  public static class TestConfiguration {
  }
}
