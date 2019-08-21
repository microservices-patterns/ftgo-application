package net.chrisrichardson.ftgo.orderservice.grpc;


import io.grpc.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;

public class OrderServiceClient {
  private static final Logger logger = Logger.getLogger(OrderServiceClient.class.getName());

  private final ManagedChannel channel;
  private final OrderServiceGrpc.OrderServiceBlockingStub clientStub;

  public OrderServiceClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
    clientStub = OrderServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public long createOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems, net.chrisrichardson.ftgo.common.Address deliveryAddress, LocalDateTime deliveryTime) {
    CreateOrderRequest.Builder builder = CreateOrderRequest.newBuilder()
            .setConsumerId(consumerId)
            .setRestaurantId(restaurantId)
            .setDeliveryAddress(makeAddress(deliveryAddress))
            .setDeliveryTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(deliveryTime));
    lineItems.forEach(li -> builder.addLineItems(LineItem.newBuilder().setQuantity(li.getQuantity()).setMenuItemId(li.getMenuItemId())));
    CreateOrderReply response = clientStub.createOrder(builder.build());
    return response.getOrderId();
  }

  private Address makeAddress(net.chrisrichardson.ftgo.common.Address address) {
    Address.Builder builder = Address.newBuilder()
            .setStreet1(address.getStreet1());
    if (address.getStreet2() != null)
      builder.setStreet2(address.getStreet2());
    builder
            .setCity(address.getCity())
            .setState(address.getState())
            .setZip(address.getZip());
    return builder.build();
  }


}
