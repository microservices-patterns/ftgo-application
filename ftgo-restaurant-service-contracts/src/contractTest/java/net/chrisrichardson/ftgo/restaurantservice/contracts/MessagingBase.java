package net.chrisrichardson.ftgo.restaurantservice.contracts;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {MessagingBase.TestConfig.class})
public abstract class MessagingBase {

    private static final String RESTAURANT_AGGREGATE_TYPE = "net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant";

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfig { }

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    // Event triggers - called by contract tests for domain events
    protected void restaurantCreatedEvent() {
        // Publish a RestaurantCreated event with address
        Map<String, Object> address = Map.of(
            "street1", "1 Main Street",
            "street2", "Unit 99",
            "city", "Oakland",
            "state", "CA",
            "zip", "94611"
        );
        Object event = Map.of("address", address);
        domainEventPublisher.publish(RESTAURANT_AGGREGATE_TYPE, 99L, Collections.singletonList(event));
    }
}
