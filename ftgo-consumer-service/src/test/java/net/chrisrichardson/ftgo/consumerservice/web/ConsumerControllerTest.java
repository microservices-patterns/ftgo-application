package net.chrisrichardson.ftgo.consumerservice.web;

import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.mock;

class ConsumerControllerTest {

  private ConsumerController consumerController;
  private ConsumerService consumerService;
  private Consumer consumer;

  @BeforeAll
  static void setUpClass() {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  @BeforeEach
  void setUp() {
    consumerService = mock(ConsumerService.class);
    consumerController = new ConsumerController(consumerService);
    consumer = new Consumer(new PersonName("x", "y"));
  }

  @Test
  void shouldGetConsumer() {
    Mockito.when(consumerService.findById(1)).thenReturn(Optional.of(consumer));
    given().
            standaloneSetup(configureControllers(consumerController)).
            when().
            get("/consumers/1").
            then().
            statusCode(200);
  }

  @Test
  void shouldReturn404WhenConsumerNotFound() {
    Mockito.when(consumerService.findById(999)).thenReturn(Optional.empty());
    given().
            standaloneSetup(configureControllers(consumerController)).
            when().
            get("/consumers/999").
            then().
            statusCode(404);
  }

  private StandaloneMockMvcBuilder configureControllers(Object... controllers) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);
    return MockMvcBuilders.standaloneSetup(controllers)
            .setMessageConverters(converter);
  }
}
