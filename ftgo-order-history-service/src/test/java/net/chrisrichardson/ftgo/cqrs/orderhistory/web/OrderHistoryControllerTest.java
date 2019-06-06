package net.chrisrichardson.ftgo.cqrs.orderhistory.web;

import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryDao;
import net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb.Order;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderHistoryControllerTest {

  private OrderHistoryDao orderHistoryDao;
  private OrderHistoryController orderHistoryController;

  @Before
  public void setUp() {
    orderHistoryDao = mock(OrderHistoryDao.class);
    orderHistoryController = new OrderHistoryController(orderHistoryDao);
  }

  @Test
  public void testGetOrder() {
    when(orderHistoryDao.findOrder("1")).thenReturn(Optional.of(new Order("1", null, null, null, null, 101L, "Ajanta")));

    given().
            standaloneSetup(configureControllers(orderHistoryController)).
            when().
            get("/orders/1").
            then().
            statusCode(200).
            body("restaurantName", equalTo("Ajanta"))
    ;

  }

  // TODO move to test library

  private StandaloneMockMvcBuilder configureControllers(Object... controllers) {
    CommonJsonMapperInitializer.registerMoneyModule();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);
    return MockMvcBuilders.standaloneSetup(controllers).setMessageConverters(converter);
  }

}