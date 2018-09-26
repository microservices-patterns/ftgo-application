package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.consumerservice.api.ConsumerServiceChannels;
import net.chrisrichardson.ftgo.consumerservice.api.ValidateOrderByConsumer;
import net.chrisrichardson.ftgo.kitchenservice.api.CancelCreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.ConfirmCreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.CreateTicket;
import net.chrisrichardson.ftgo.kitchenservice.api.KitchenServiceChannels;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.*;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_ID;

public class CreateOrderSagaTest {

  private OrderServiceProxy orderServiceProxy = new OrderServiceProxy();
  private KitchenServiceProxy kitchenServiceProxy = new KitchenServiceProxy();
  private ConsumerServiceProxy consumerServiceProxy = new ConsumerServiceProxy();
  private AccountingServiceProxy accountingServiceProxy = new AccountingServiceProxy();

  @BeforeClass
  public static void initialize() {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  private CreateOrderSaga makeCreateOrderSaga() {
    return new CreateOrderSaga(orderServiceProxy, consumerServiceProxy, kitchenServiceProxy, accountingServiceProxy);
  }

  @Test
  public void shouldCreateOrder() {
    given()
        .saga(makeCreateOrderSaga(),
                new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS)).
    expect().
        command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID,
                CHICKEN_VINDALOO_ORDER_TOTAL)).
        to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new CreateTicket(AJANTA_ID, ORDER_ID, null /* FIXME */)).
      to(KitchenServiceChannels.kitchenServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(AccountingServiceChannels.accountingServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new ConfirmCreateTicket(ORDER_ID)).
      to(KitchenServiceChannels.kitchenServiceChannel).
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
                new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS)).
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
                    new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS)).
    expect().
      command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID,
              CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new CreateTicket(AJANTA_ID, ORDER_ID, null /* FIXME */)).
      to(KitchenServiceChannels.kitchenServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(AccountingServiceChannels.accountingServiceChannel).
    andGiven().
      failureReply().
    expect().
      command(new CancelCreateTicket(ORDER_ID)).
      to(KitchenServiceChannels.kitchenServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new RejectOrderCommand(ORDER_ID)).
      to(OrderServiceChannels.orderServiceChannel)
    ;
  }
}