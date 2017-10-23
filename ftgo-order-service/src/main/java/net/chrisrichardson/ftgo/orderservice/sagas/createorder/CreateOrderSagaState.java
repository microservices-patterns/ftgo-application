package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

public enum CreateOrderSagaState {
  VERIFYING_CONSUMER, VERIFYING_RESTAURANT, RESTAURANT_VERIFIED, AUTHORIZING, AUTHORIZED, REJECTED
}
