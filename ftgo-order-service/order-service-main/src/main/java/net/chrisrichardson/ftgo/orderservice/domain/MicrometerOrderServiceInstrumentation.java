package net.chrisrichardson.ftgo.orderservice.domain;

import io.micrometer.core.instrument.MeterRegistry;

public class MicrometerOrderServiceInstrumentation implements OrderServiceInstrumentation {

    private final MeterRegistry meterRegistry;

    public MicrometerOrderServiceInstrumentation(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void noteOrderPlaced() {
        meterRegistry.counter("placed_orders").increment();
    }

    @Override
    public void noteOrderApproved() {
        meterRegistry.counter("approved_orders").increment();
    }

    @Override
    public void noteOrderRejected() {
        meterRegistry.counter("rejected_orders").increment();
    }
}
