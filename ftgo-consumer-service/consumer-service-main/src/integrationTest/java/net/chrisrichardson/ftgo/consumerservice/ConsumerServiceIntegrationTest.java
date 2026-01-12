package net.chrisrichardson.ftgo.consumerservice;

import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.spring.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.testutil.TestMessageConsumer;
import io.eventuate.tram.testutil.TestMessageConsumerFactory;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerServiceConfiguration;
import net.chrisrichardson.ftgo.consumerservice.web.CreateConsumerRequest;
import net.chrisrichardson.ftgo.consumerservice.web.ConsumerWebConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ConsumerServiceIntegrationTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConsumerServiceIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Configuration
  @Import({ConsumerServiceConfiguration.class,
          ConsumerWebConfiguration.class,
          TramCommandProducerConfiguration.class,
          TramInMemoryConfiguration.class})
  @EnableAutoConfiguration
  public static class TestConfiguration {

    @Bean
    public TestMessageConsumerFactory testMessageConsumerFactory() {
      return new TestMessageConsumerFactory();
    }
  }

  private String baseUrl(String path) {
    return "http://localhost:" + port + path;
  }

  @Autowired
  private CommandProducer commandProducer;

  @Autowired
  private TestMessageConsumerFactory testMessageConsumerFactory;

  @Test
  void shouldCreateConsumerAndValidateOrder() {
    String postUrl = baseUrl("/consumers");

    Integer consumerId =
            given().
            body(new CreateConsumerRequest(new PersonName("John", "Doe"))).
            contentType("application/json").
            when().
            post(postUrl).
            then().
            statusCode(200).
            extract().
            path("consumerId");

    assertNotNull(consumerId);

    TestMessageConsumer testMessageConsumer = testMessageConsumerFactory.make();

    long orderId = 999;
    Money orderTotal = new Money(123);

    String messageId = commandProducer.send("consumerService", null,
            new ValidateOrderByConsumer(consumerId, orderId, orderTotal),
            testMessageConsumer.getReplyChannel(), Collections.emptyMap());

    testMessageConsumer.assertHasReplyTo(messageId);
  }
}
