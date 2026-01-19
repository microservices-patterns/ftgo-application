package net.chrisrichardson.ftgo.orderservice.domain;

public interface OrderServiceInstrumentation {
    void noteOrderPlaced();
    void noteOrderApproved();
    void noteOrderRejected();
}
