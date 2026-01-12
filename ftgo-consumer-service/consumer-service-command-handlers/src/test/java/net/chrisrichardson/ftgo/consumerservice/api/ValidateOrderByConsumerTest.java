package net.chrisrichardson.ftgo.consumerservice.api;

import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.common.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateOrderByConsumerTest {

  @BeforeAll
  static void setUp() {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  @Test
  void shouldDeserialize() {
    String json = """
        {
          "consumerId": 1,
          "orderId": 2,
          "orderTotal": "12.34"
        }
        """;

    ValidateOrderByConsumer cmd = JSonMapper.fromJson(json, ValidateOrderByConsumer.class);

    assertEquals(1, cmd.getConsumerId());
    assertEquals(2, cmd.getOrderId());
    assertEquals(new Money("12.34"), cmd.getOrderTotal());
  }
}
