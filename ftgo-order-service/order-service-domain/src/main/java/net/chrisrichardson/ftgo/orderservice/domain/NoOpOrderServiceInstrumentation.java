package net.chrisrichardson.ftgo.orderservice.domain;

public class NoOpOrderServiceInstrumentation implements OrderServiceInstrumentation {
    @Override
    public void noteOrderPlaced() {
    }

    @Override
    public void noteOrderApproved() {
    }

    @Override
    public void noteOrderRejected() {
    }
}
