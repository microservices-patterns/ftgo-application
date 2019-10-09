package net.chrisrichardson.ftgo.orderservice.grpc;


import net.chrisrichardson.ftgo.orderservice.OrderDetailsMother;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceGrpIntegrationTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OrderServiceGrpIntegrationTest {

  @Autowired
  private OrderService orderService;

  @Test
  public void shouldCreateOrder() {

    Order order = OrderDetailsMother.CHICKEN_VINDALOO_ORDER;

    when(orderService.createOrder(any(Long.class), any(Long.class), any(DeliveryInformation.class), any(List.class))).thenReturn(order);

    OrderServiceClient client = new OrderServiceClient("localhost", 50051);

    List<MenuItemIdAndQuantity> expectedLineItems = order.getLineItems().stream().map(li -> new MenuItemIdAndQuantity(li.getMenuItemId(), li.getQuantity())).collect(Collectors.toList());

    long orderId = client.createOrder(order.getConsumerId(), order.getRestaurantId(), expectedLineItems, order.getDeliveryInformation().getDeliveryAddress(), order.getDeliveryInformation().getDeliveryTime());

    assertEquals((long)order.getId(), orderId);

    verify(orderService).createOrder(order.getConsumerId(), order.getRestaurantId(), order.getDeliveryInformation(), expectedLineItems);

  }
}
