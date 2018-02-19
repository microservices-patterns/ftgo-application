package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceChannels;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CancelCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderServiceChannels;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_ORDER_DETAILS;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_ORDER_TOTAL;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CONSUMER_ID;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.ORDER_ID;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static net.chrisrichardson.ftgo.orderservice.sagas.createorder.MockSagaTest.*;

public class CreateOrderSagaTest {

  private OrderServiceProxy orderServiceProxy = new OrderServiceProxy();
  private RestaurantOrderServiceProxy restaurantOrderServiceProxy = new RestaurantOrderServiceProxy();
  private ConsumerServiceProxy consumerServiceProxy = new ConsumerServiceProxy();
  private AccountingServiceProxy accountingServiceProxy = new AccountingServiceProxy();

  @BeforeClass
  public static void initialize() {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  private CreateOrderSaga makeCreateOrderSaga() {
    return new CreateOrderSaga(orderServiceProxy, consumerServiceProxy, restaurantOrderServiceProxy, accountingServiceProxy);
  }

  @Test
  public void shouldCreateOrder() {
    given()
        .saga(makeCreateOrderSaga(),
                new CreateOrderSagaData(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS)).
    expect().
        command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID,
                CHICKEN_VINDALOO_ORDER_TOTAL)).
        to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new CreateRestaurantOrder(AJANTA_ID, ORDER_ID, null /* FIXME */)).
      to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(AccountingServiceChannels.accountingServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new ConfirmCreateRestaurantOrder(ORDER_ID)).
      to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new ApproveOrderCommand(ORDER_ID)).
      to(OrderServiceChannels.orderServiceChannel)
            ;
  }

  @Test
  public void shouldRejectOrderDueToConsumerVerificationFailed() {
    given()
        .saga(makeCreateOrderSaga(),
                new CreateOrderSagaData(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS)).
    expect().
        command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID,
                CHICKEN_VINDALOO_ORDER_TOTAL)).
        to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
        failureReply().
    expect().
        command(new RejectOrderCommand(ORDER_ID)).
        to(OrderServiceChannels.orderServiceChannel);
  }

  @Test
  public void shouldRejectDueToFailedAuthorizxation() {
    given()
            .saga(makeCreateOrderSaga(),
                    new CreateOrderSagaData(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS)).
    expect().
      command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID,
              CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new CreateRestaurantOrder(AJANTA_ID, ORDER_ID, null /* FIXME */)).
      to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(AccountingServiceChannels.accountingServiceChannel).
    andGiven().
      failureReply().
    expect().
      command(new CancelCreateRestaurantOrder(ORDER_ID)).
      to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new RejectOrderCommand(ORDER_ID)).
      to(OrderServiceChannels.orderServiceChannel)
    ;
  }
}