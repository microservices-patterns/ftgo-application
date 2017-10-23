package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.ResultWithEvents;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderLineItem;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

public class OrderTest {

  @Test
  public void shouldReviseOrder() {
    Order order = Order.createOrder(101L, 102L, Collections.singletonList(new OrderLineItem("1", "Chicken Vindaloo", new Money(4), 5))).result;

    order.noteAuthorized();

    OrderRevision orderRevision = new OrderRevision(Optional.empty(), Collections.singletonMap("1", 10));

    ResultWithEvents<LineItemQuantityChange> result = order.revise(orderRevision);

    assertEquals(new Money(4).multiply(10), result.result.getNewOrderTotal());


    order.confirmRevision(orderRevision);

    assertEquals(new Money(4).multiply(10), order.getOrderTotal());
  }
}