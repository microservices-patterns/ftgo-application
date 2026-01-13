package net.chrisrichardson.ftgo.restaurantservice;

import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.common.MoneyModule;
import net.chrisrichardson.ftgo.restaurantservice.domain.CreateRestaurantRequest;
import net.chrisrichardson.ftgo.restaurantservice.domain.MenuItem;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantMenu;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantServiceDomainConfiguration;
import net.chrisrichardson.ftgo.restaurantservice.web.RestaurantWebConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RestaurantServiceIntegrationTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestaurantServiceIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Configuration
  @Import({RestaurantServiceDomainConfiguration.class,
          RestaurantWebConfiguration.class,
          TramInMemoryConfiguration.class})
  @EnableAutoConfiguration
  public static class TestConfiguration {
  }

  @BeforeEach
  void setUp() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule());
    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
            ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory((type, s) -> objectMapper));
  }

  private String baseUrl(String path) {
    return "http://localhost:" + port + path;
  }

  @Test
  void shouldCreateRestaurantAndRetrieveIt() {
    String postUrl = baseUrl("/restaurants");

    Address address = new Address("1 Main Street", "Unit 99", "Oakland", "CA", "94611");
    MenuItem menuItem = new MenuItem("1", "Chicken Vindaloo", new Money("12.34"));
    RestaurantMenu menu = new RestaurantMenu(Collections.singletonList(menuItem));
    CreateRestaurantRequest request = new CreateRestaurantRequest("Ajanta", address, menu);

    Integer restaurantId =
            given().
            body(request).
            contentType("application/json").
            when().
            post(postUrl).
            then().
            statusCode(200).
            extract().
            path("id");

    assertNotNull(restaurantId);

    String getUrl = baseUrl("/restaurants/" + restaurantId);

    given().
    when().
    get(getUrl).
    then().
    statusCode(200).
    body("id", equalTo(restaurantId)).
    body("name", equalTo("Ajanta"));
  }

  @Test
  void shouldReturn404ForNonExistentRestaurant() {
    String getUrl = baseUrl("/restaurants/99999");

    given().
    when().
    get(getUrl).
    then().
    statusCode(404);
  }
}
