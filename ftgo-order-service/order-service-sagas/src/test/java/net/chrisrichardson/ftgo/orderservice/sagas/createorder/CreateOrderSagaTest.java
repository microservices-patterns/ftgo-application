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
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderService;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import static io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.*;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateOrderSagaTest {

  private KitchenServiceProxy kitchenServiceProxy = new KitchenServiceProxy();
  private ConsumerServiceProxy consumerServiceProxy = new ConsumerServiceProxy();
  private AccountingServiceProxy accountingServiceProxy = new AccountingServiceProxy();

  private OrderService orderService;
  private Order order;

  @BeforeAll
  public static void initialize() {
    CommonJsonMapperInitializer.registerMoneyModule();
  }

  @BeforeEach
  public void setUp() {
    orderService = mock(OrderService.class);
  }

  private CreateOrderSaga makeCreateOrderSaga() {
    return new CreateOrderSaga(orderService, consumerServiceProxy, kitchenServiceProxy, accountingServiceProxy);
  }

  @Test
  public void shouldCreateOrder() {
    when(orderService.createOrder(CONSUMER_ID, AJANTA_ID, DELIVERY_INFORMATION, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES))
            .then((Answer<Order>) invocation -> {
              order = CHICKEN_VINDALOO_ORDER;
              return order;
            });

    given()
        .saga(makeCreateOrderSaga(),
                new CreateOrderSagaState(CONSUMER_ID, AJANTA_ID, DELIVERY_INFORMATION, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES)).
    expect().
        command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
        to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new CreateTicket(AJANTA_ID, ORDER_ID, null /* FIXME */)).
      to(KitchenServiceChannels.COMMAND_CHANNEL).
    andGiven().
        successReply().
    expect().
      command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(AccountingServiceChannels.accountingServiceChannel).
    andGiven().
        successReply().
    expect().
      command(new ConfirmCreateTicket(ORDER_ID)).
      to(KitchenServiceChannels.COMMAND_CHANNEL).
    andGiven().
        successReply().
    expectCompletedSuccessfully();
  }

  @Test
  public void shouldRejectOrderDueToConsumerVerificationFailed() {
    when(orderService.createOrder(CONSUMER_ID, AJANTA_ID, DELIVERY_INFORMATION, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES))
            .then((Answer<Order>) invocation -> {
              order = CHICKEN_VINDALOO_ORDER;
              return order;
            });

    given()
        .saga(makeCreateOrderSaga(),
                new CreateOrderSagaState(CONSUMER_ID, AJANTA_ID, DELIVERY_INFORMATION, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES)).
    expect().
        command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
        to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
        failureReply().
    expectRolledBack();
  }

  @Test
  public void shouldRejectDueToFailedAuthorization() {
    when(orderService.createOrder(CONSUMER_ID, AJANTA_ID, DELIVERY_INFORMATION, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES))
            .then((Answer<Order>) invocation -> {
              order = CHICKEN_VINDALOO_ORDER;
              return order;
            });

    given()
            .saga(makeCreateOrderSaga(),
                    new CreateOrderSagaState(CONSUMER_ID, AJANTA_ID, DELIVERY_INFORMATION, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES)).
    expect().
      command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(ConsumerServiceChannels.consumerServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new CreateTicket(AJANTA_ID, ORDER_ID, null /* FIXME */)).
      to(KitchenServiceChannels.COMMAND_CHANNEL).
    andGiven().
      successReply().
    expect().
      command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL)).
      to(AccountingServiceChannels.accountingServiceChannel).
    andGiven().
      failureReply().
    expect().
      command(new CancelCreateTicket(ORDER_ID)).
      to(KitchenServiceChannels.COMMAND_CHANNEL).
    andGiven().
      successReply().
    expectRolledBack();
  }
}
