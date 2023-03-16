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
  public ResponseEntity<GetAccountResponse> accountServiceGetAccount(@PathVariable long accountId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8085/accounts/" + accountId, ResponseEntity.class);
  }

  @RequestMapping(path = "consumer-service/consumers", method = RequestMethod.POST)
  public CreateConsumerResponse consumerServicePostConsumer(
      @RequestBody CreateConsumerRequest request) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8081/consumers", request, CreateConsumerResponse.class)
        .getBody();
  }

  @RequestMapping(path = "consumer-service/consumers/{consumerId}", method = RequestMethod.GET)
  public ResponseEntity<GetConsumerResponse> consumerServiceGetConsumer(
      @PathVariable long consumerId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8081/consumers/" + consumerId, ResponseEntity.class);
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
  public ResponseEntity<DeliveryStatus> deliveryServiceGetDeliveryStatus(
      @PathVariable long deliveryId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8089/deliveries/" + deliveryId, ResponseEntity.class);
  }

  @RequestMapping(path = "kitchen-service/tickets/{ticketId}/accept", method = RequestMethod.POST)
  public void acceptTicket(
      @PathVariable long ticketId, @RequestBody TicketAcceptance ticketAcceptance) {
    restTemplate.postForEntity(
        "http://192.168.1.8:8083/tickets/" + ticketId + "/accept", ticketAcceptance, Object.class);
  }

  @RequestMapping(path = "kitchen-service/restaurants/{restaurantId}", method = RequestMethod.GET)
  public ResponseEntity<GetRestaurantResponse> getRestaurant(@PathVariable long restaurantId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8083/restaurants" + restaurantId, ResponseEntity.class);
  }

  @RequestMapping(path = "order-history-service/orders/", method = RequestMethod.GET)
  public ResponseEntity<GetOrdersResponse> orderHistoryServiceGetOrders(
      @RequestParam(name = "consumerId") String consumerId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8086/orders?consumerId=" + consumerId, ResponseEntity.class);
  }

  @RequestMapping(path = "order-history-service/orders/{orderId}", method = RequestMethod.GET)
  public ResponseEntity<GetOrderResponse> orderHistoryServiceGetOrder(
      @PathVariable String orderId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8086/orders/" + orderId, ResponseEntity.class);
  }

  @RequestMapping(path = "order-service/orders", method = RequestMethod.POST)
  public CreateOrderResponse orderServiceCreate(@RequestBody CreateOrderRequest request) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8082/orders/", request, CreateOrderResponse.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/orders/{orderId}", method = RequestMethod.GET)
  public ResponseEntity<GetOrderResponseNew> orderServiceGetOrder(@PathVariable long orderId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8082/orders/" + orderId, ResponseEntity.class);
  }

  @RequestMapping(path = "order-service/orders/{orderId}/cancel", method = RequestMethod.POST)
  public ResponseEntity<GetOrderResponseNew> orderServiceCancel(@PathVariable long orderId) {
    return restTemplate
        .postForEntity(
            "http://192.168.1.8:8082/orders/" + orderId + "/cancel", null, ResponseEntity.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/orders/{orderId}/revise", method = RequestMethod.POST)
  public ResponseEntity<GetOrderResponseNew> orderServiceRevise(
      @PathVariable long orderId, @RequestBody ReviseOrderRequest request) {
    return restTemplate
        .postForEntity(
            "http://192.168.1.8:8082/orders/" + orderId + "/revise", request, ResponseEntity.class)
        .getBody();
  }

  @RequestMapping(path = "order-service/restaurants/{restaurantId}", method = RequestMethod.POST)
  public ResponseEntity<GetOrderResponseNew> orderServiceGetRestaurant(
      @PathVariable long restaurantId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8082/restaurants/" + restaurantId, ResponseEntity.class);
  }

  @RequestMapping(path = "restaurant-service/restaurants", method = RequestMethod.POST)
  public ResponseEntity<GetOrderResponse> restaurantServiceCreate(
      @RequestBody ReviseOrderRequest request) {
    return restTemplate
        .postForEntity("http://192.168.1.8:8084/restaurants/", request, ResponseEntity.class)
        .getBody();
  }

  @RequestMapping(
      path = "restaurant-service/restaurants/{restaurantId}",
      method = RequestMethod.GET)
  public ResponseEntity<GetRestaurantResponseNew> restaurantServiceGet(
      @PathVariable long restaurantId) {
    return restTemplate.getForObject(
        "http://192.168.1.8:8084/restaurants/" + restaurantId, ResponseEntity.class);
  }
}
