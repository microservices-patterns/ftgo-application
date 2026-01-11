package net.chrisrichardson.ftgo.orderservice.contracts;

import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

/**
 * Base class for contract verification tests.
 * Currently disabled - will be enabled when provider-side contract testing is implemented.
 */
@SpringBootTest(classes = MessagingBase.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class MessagingBase {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfiguration {
    }

    // This method will be called by the generated contract test
    // to trigger the OrderCreatedEvent for delivery service
    protected void orderCreatedEvent() {
        // TODO: Implement when provider-side testing is enabled
        throw new UnsupportedOperationException("Provider-side contract testing not yet implemented");
    }

    // This method will be called by the generated contract test
    // to trigger the general OrderCreatedEvent
    protected void orderCreated() {
        // TODO: Implement when provider-side testing is enabled
        throw new UnsupportedOperationException("Provider-side contract testing not yet implemented");
    }
}
