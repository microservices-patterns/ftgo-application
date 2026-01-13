package net.chrisrichardson.ftgo.restaurantservice.contracts;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

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
        Address address = new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611");
        MenuItem menuItem = new MenuItem("1", "Chicken Vindaloo", new Money("12.34"));
        RestaurantMenu menu = new RestaurantMenu(Collections.singletonList(menuItem));
        RestaurantCreated event = new RestaurantCreated("Ajanta", address, menu);
        List<DomainEvent> events = Collections.singletonList(event);
        domainEventPublisher.publish(RESTAURANT_AGGREGATE_TYPE, 99L, events);
    }
}
