package net.chrisrichardson.ftgo.orderservice.api.events;

public enum OrderState {
  CREATE_PENDING,
  AUTHORIZED,
  REJECTED,
  CANCEL_PENDING,
  CANCELLED,
  REVISION_PENDING,
}
