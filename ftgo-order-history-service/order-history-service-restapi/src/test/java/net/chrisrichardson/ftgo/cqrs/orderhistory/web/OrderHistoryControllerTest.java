package net.chrisrichardson.ftgo.cqrs.orderhistory.web;

import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.cqrs.orderhistory.Order;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderHistoryControllerTest {

  private OrderHistoryDao orderHistoryDao;
  private OrderHistoryController orderHistoryController;

  @BeforeEach
  void setUp() {
    orderHistoryDao = mock(OrderHistoryDao.class);
    orderHistoryController = new OrderHistoryController(orderHistoryDao);
  }

  @Test
  void testGetOrder() {
    when(orderHistoryDao.findOrder("1")).thenReturn(Optional.of(new Order("1", null, null, null, null, 101L, "Ajanta")));

    given().
            standaloneSetup(configureControllers(orderHistoryController)).
            when().
            get("/orders/1").
            then().
            statusCode(200).
            body("restaurantName", equalTo("Ajanta"));
  }

  private StandaloneMockMvcBuilder configureControllers(Object... controllers) {
    CommonJsonMapperInitializer.registerMoneyModule();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);
    return MockMvcBuilders.standaloneSetup(controllers).setMessageConverters(converter);
  }
}
