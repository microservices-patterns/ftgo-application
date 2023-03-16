package net.chrisrichardson.ftgo.consumerservice.web;

import io.eventuate.tram.events.publisher.ResultWithEvents;
import net.chrisrichardson.ftgo.consumerservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CourierAvailability;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CreateOrderRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.CreateOrderResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.DeliveryStatus;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetAccountResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetOrderResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetOrderResponseNew;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetOrdersResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetRestaurantResponse;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.GetRestaurantResponseNew;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.ReviseOrderRequest;
import net.chrisrichardson.ftgo.consumerservice.domain.swagger.TicketAcceptance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

  @Autowired RestTemplate restTemplate;

  private ConsumerService consumerService;

  public ConsumerController(ConsumerService consumerService) {
    this.consumerService = consumerService;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/consumers")
  public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
    ResultWithEvents<Consumer> result = consumerService.create(request.getName());
    return new CreateConsumerResponse(result.result.getId());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/consumers/{consumerId}")
  public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
    return consumerService
        .findById(consumerId)
        .map(
            consumer ->
                new ResponseEntity<>(new GetConsumerResponse(consumer.getName()), HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(path = "account-service/accounts/{accountId}", method = RequestMethod.GET)
  public Object accountServiceGetAccount(@PathVariable long accountId) {
    return restTemplate.getForObject("http://192.168.1.8:8085/accounts/" + accountId, Object.class);
  }

  @RequestMapping(path = "consumer-service/consumers", method = RequestMethod.POST)
  public CreateConsumerResponse consumerServicePostConsumer(
      @RequestBody CreateConsumerRequest request) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8081/consumers", request, CreateConsumerResponse.class)
        .getBody();
  }

  @RequestMapping(path = "consumer-service/consumers/{consumerId}", method = RequestMethod.GET)
  public Object consumerServiceGetConsumer(@PathVariable long consumerId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8081/consumers/" + consumerId, Object.class);
  }

  @RequestMapping(
      path = "delivery-service/couriers/{courierId}/availability",
      method = RequestMethod.POST)
  public void deliveryServiceUpdateCourierLocation(
      @PathVariable long courierId, @RequestBody CourierAvailability availability) {
    restTemplate.postForEntity(
        "http://192.168.1.8:8089/couriers/" + courierId + "/availability",
        availability,
        Object.class);
  }

  @RequestMapping(path = "delivery-service/deliveries/{deliveryId}", method = RequestMethod.GET)
  public Object deliveryServiceGetDeliveryStatus(@PathVariable long deliveryId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8089/deliveries/" + deliveryId, Object.class);
  }

  @RequestMapping(path = "kitchen-service/tickets/{ticketId}/accept", method = RequestMethod.POST)
  public void acceptTicket(
      @PathVariable long ticketId, @RequestBody TicketAcceptance ticketAcceptance) {
    restTemplate.postForEntity(
        "http://192.168.1.8:8083/tickets/" + ticketId + "/accept", ticketAcceptance, Object.class);
  }

  @RequestMapping(path = "kitchen-service/restaurants/{restaurantId}", method = RequestMethod.GET)
  public Object getRestaurant(@PathVariable long restaurantId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8083/restaurants" + restaurantId, Object.class);
  }

  @RequestMapping(path = "order-history-service/orders/", method = RequestMethod.GET)
  public Object orderHistoryServiceGetOrders(@RequestParam(name = "consumerId") String consumerId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8086/orders?consumerId=" + consumerId, Object.class);
  }

  @RequestMapping(path = "order-history-service/orders/{orderId}", method = RequestMethod.GET)
  public Object orderHistoryServiceGetOrder(@PathVariable String orderId) {
    return restTemplate.getForObject("http://192.168.1.8:8086/orders/" + orderId, Object.class);
  }

  @RequestMapping(path = "order-service/orders", method = RequestMethod.POST)
  public CreateOrderResponse orderServiceCreate(@RequestBody CreateOrderRequest request) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8082/orders/", request, CreateOrderResponse.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/orders/{orderId}", method = RequestMethod.GET)
  public Object orderServiceGetOrder(@PathVariable long orderId) {
    return restTemplate.getForObject("http://192.168.1.8:8082/orders/" + orderId, Object.class);
  }

  @RequestMapping(path = "order-service/orders/{orderId}/cancel", method = RequestMethod.POST)
  public Object orderServiceCancel(@PathVariable long orderId) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8082/orders/" + orderId + "/cancel", null, Object.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/orders/{orderId}/revise", method = RequestMethod.POST)
  public Object orderServiceRevise(
      @PathVariable long orderId, @RequestBody ReviseOrderRequest request) {
    return restTemplate
        .postForEntity(
            "http://192.168.1.8:8082/orders/" + orderId + "/revise", request, Object.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/restaurants/{restaurantId}", method = RequestMethod.POST)
  public Object orderServiceGetRestaurant(@PathVariable long restaurantId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8082/restaurants/" + restaurantId, Object.class);
  }

  @RequestMapping(path = "restaurant-service/restaurants", method = RequestMethod.POST)
  public Object restaurantServiceCreate(@RequestBody ReviseOrderRequest request) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8084/restaurants/", request, Object.class)
        .getBody();
  }

  @RequestMapping(
      path = "restaurant-service/restaurants/{restaurantId}",
      method = RequestMethod.GET)
  public Object restaurantServiceGet(@PathVariable long restaurantId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8084/restaurants/" + restaurantId, Object.class);
  }
}
