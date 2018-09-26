package net.chrisrichardson.ftgo.cqrs.orderhistory.web;

import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistory;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryDao;
import net.chrisrichardson.ftgo.cqrs.orderhistory.OrderHistoryFilter;
import net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/orders")
public class OrderHistoryController {

  private OrderHistoryDao orderHistoryDao;

  public OrderHistoryController(OrderHistoryDao orderHistoryDao) {
    this.orderHistoryDao = orderHistoryDao;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<GetOrdersResponse> getOrders(@RequestParam(name = "consumerId") String consumerId) {
    OrderHistory orderHistory = orderHistoryDao.findOrderHistory(consumerId, new OrderHistoryFilter());
    return new ResponseEntity<>(new GetOrdersResponse(orderHistory.getOrders()
            .stream()
            .map(this::makeGetOrderResponse).collect(toList()), orderHistory.getStartKey().orElse(null)), HttpStatus.OK);
  }

  private GetOrderResponse makeGetOrderResponse(Order order) {
    return new GetOrderResponse(order.getOrderId(), order.getStatus(), order.getRestaurantId(), order.getRestaurantName());
  }

  @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
  public ResponseEntity<GetOrderResponse> getOrder(@PathVariable String orderId) {
    return orderHistoryDao.findOrder(orderId)
            .map(order -> new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
