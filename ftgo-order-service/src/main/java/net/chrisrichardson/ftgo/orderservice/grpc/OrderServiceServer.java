package net.chrisrichardson.ftgo.orderservice.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.web.MenuItemIdAndQuantity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class OrderServiceServer {
  private static final Logger logger = LoggerFactory.getLogger(OrderServiceServer.class);

  private int port = 50051;
  private Server server;
  private OrderService orderService;

  public OrderServiceServer(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostConstruct
  public void start() throws IOException {
    server = ServerBuilder.forPort(port)
            .addService(new OrderServiceImpl())
            .build()
            .start();
    logger.info("Server started, listening on " + port);
  }

  @PreDestroy
  public void stop() {
    if (server != null) {
      logger.info("*** shutting down gRPC server since JVM is shutting down");
      server.shutdown();
      logger.info("*** server shut down");
    }
  }


  private class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @Override
    public void createOrder(CreateOrderRequest req, StreamObserver<CreateOrderReply> responseObserver) {
      List<LineItem> lineItemsList = req.getLineItemsList();
      Order order = orderService.createOrder(req.getConsumerId(),
              req.getRestaurantId(),
              new DeliveryInformation(LocalDateTime.parse(req.getDeliveryTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), makeAddress(req.getDeliveryAddress())),
              lineItemsList.stream().map(x -> new MenuItemIdAndQuantity(x.getMenuItemId(), x.getQuantity())).collect(toList())
      );
      CreateOrderReply reply = CreateOrderReply.newBuilder().setOrderId(order.getId()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    private Address makeAddress(net.chrisrichardson.ftgo.orderservice.grpc.Address address) {
      return new Address(address.getStreet1(), nullIfBlank(address.getStreet2()), address.getCity(), address.getState(), address.getZip());
    }

    @Override
    public void cancelOrder(CancelOrderRequest req, StreamObserver<CancelOrderReply> responseObserver) {
      CancelOrderReply reply = CancelOrderReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void reviseOrder(ReviseOrderRequest req, StreamObserver<ReviseOrderReply> responseObserver) {
      ReviseOrderReply reply = ReviseOrderReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

  private String nullIfBlank(String s) {
    return StringUtils.isBlank(s) ? null : s;
  }
}
