package net.chrisrichardson.ftgo.endtoendtests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import net.chrisrichardson.ftgo.endtoendtests.dto.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EndToEndTests {

    public static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";
    public static final String RESTAURANT_NAME = "My Restaurant";

    private static ApplicationUnderTest aut;
    private static String host;
    private static int consumerPort;
    private static int orderPort;
    private static int accountingPort;
    private static int restaurantsPort;
    private static int kitchenPort;
    private static int apiGatewayPort;
    private static int deliveryServicePort;

    private final int revisedQuantityOfChickenVindaloo = 10;
    private final Money priceOfChickenVindaloo = new Money("12.34");

    private int consumerId;
    private int restaurantId;
    private int orderId;
    private long courierId;

    @BeforeAll
    public static void initialize() {
        // Create and start the application under test
        aut = ApplicationUnderTest.make();
        aut.start();

        // Get service ports from the AUT
        host = aut.getHost();
        consumerPort = aut.getConsumerServicePort();
        orderPort = aut.getOrderServicePort();
        accountingPort = aut.getAccountingServicePort();
        restaurantsPort = aut.getRestaurantServicePort();
        kitchenPort = aut.getKitchenServicePort();
        apiGatewayPort = aut.getApiGatewayPort();
        deliveryServicePort = aut.getDeliveryServicePort();

        // Configure REST Assured with Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @AfterAll
    public static void cleanup() {
        if (aut != null) {
            aut.stop();
        }
    }

    private String baseUrl(int port, String path, String... pathElements) {
        assertNotNull(host, "host");

        StringBuilder sb = new StringBuilder("http://");
        sb.append(host);
        sb.append(":");
        sb.append(port);
        sb.append("/");
        sb.append(path);

        for (String pe : pathElements) {
            sb.append("/");
            sb.append(pe);
        }
        String s = sb.toString();
        System.out.println("url=" + s);
        return s;
    }

    private String consumerBaseUrl(String... pathElements) {
        return baseUrl(consumerPort, "consumers", pathElements);
    }

    private String accountingBaseUrl(String... pathElements) {
        return baseUrl(accountingPort, "accounts", pathElements);
    }

    private String restaurantBaseUrl(String... pathElements) {
        return baseUrl(restaurantsPort, "restaurants", pathElements);
    }

    private String kitchenRestaurantBaseUrl(String... pathElements) {
        return kitchenServiceBaseUrl("restaurants", pathElements);
    }

    private String kitchenServiceBaseUrl(String first, String... pathElements) {
        return baseUrl(kitchenPort, first, pathElements);
    }

    private String orderBaseUrl(String... pathElements) {
        return baseUrl(apiGatewayPort, "orders", pathElements);
    }

    private String deliveryServiceBaseUrl(String first, String... pathElements) {
        return baseUrl(deliveryServicePort, first, pathElements);
    }

    private String orderRestaurantBaseUrl(String... pathElements) {
        return baseUrl(orderPort, "restaurants", pathElements);
    }

    private String orderHistoryBaseUrl(String... pathElements) {
        return baseUrl(apiGatewayPort, "orders", pathElements);
    }

    @Test
    public void shouldCreateReviseAndCancelOrder() {
        createOrder();
        reviseOrder();
        cancelOrder();
    }

    @Test
    public void shouldDeliverOrder() {
        createOrder();
        noteCourierAvailable();
        acceptTicket();
        assertOrderAssignedToCourier();
    }

    @Test
    public void testSwaggerUiUrls() {
        testSwaggerUiUrl(consumerPort);
        testSwaggerUiUrl(orderPort);
        testSwaggerUiUrl(restaurantsPort);
        testSwaggerUiUrl(aut.getOrderHistoryServicePort());
    }

    private void testSwaggerUiUrl(int port) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        given()
                                .when()
                                .get(baseUrl(port, "swagger-ui/index.html"))
                                .then()
                                .statusCode(200));
    }

    private void reviseOrder() {
        reviseOrder(orderId);
        verifyOrderRevised(orderId);
    }

    private void verifyOrderRevised(int orderId) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    String orderTotal = given()
                            .when()
                            .get(baseUrl(orderPort, "orders", Integer.toString(orderId)))
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("orderTotal");
                    assertEquals(priceOfChickenVindaloo.multiply(revisedQuantityOfChickenVindaloo).asString(), orderTotal);
                });

        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    String state = given()
                            .when()
                            .get(orderBaseUrl(Integer.toString(orderId)))
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("orderInfo.state");
                    assertEquals("APPROVED", state);
                });
    }

    private void reviseOrder(int orderId) {
        given()
                .body(new ReviseOrderRequest(Collections.singletonList(
                        new RevisedOrderLineItem(revisedQuantityOfChickenVindaloo, CHICKEN_VINDALOO_MENU_ITEM_ID))))
                .contentType("application/json")
                .when()
                .post(orderBaseUrl(Integer.toString(orderId), "revise"))
                .then()
                .statusCode(200);
    }

    private void createOrder() {
        consumerId = createConsumer();
        verifyAccountCreatedForConsumer(consumerId);
        restaurantId = createRestaurant();
        verifyRestaurantCreatedInKitchenService(restaurantId);
        verifyRestaurantCreatedInOrderService(restaurantId);
        orderId = createOrder(consumerId, restaurantId);
        verifyOrderAuthorized(orderId);
        verifyOrderHistoryUpdated(orderId, consumerId, "APPROVED");
    }

    private void cancelOrder() {
        cancelOrder(orderId);
        verifyOrderCancelled(orderId);
        verifyOrderHistoryUpdated(orderId, consumerId, "CANCELLED");
    }

    private void verifyOrderCancelled(int orderId) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    String state = given()
                            .when()
                            .get(orderBaseUrl(Integer.toString(orderId)))
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("orderInfo.state");
                    assertEquals("CANCELLED", state);
                });
    }

    private void cancelOrder(int orderId) {
        given()
                .body("{}")
                .contentType("application/json")
                .when()
                .post(orderBaseUrl(Integer.toString(orderId), "cancel"))
                .then()
                .statusCode(200);
    }

    private Integer createConsumer() {
        Integer consumerId = given()
                .body(new CreateConsumerRequest()
                        .name(new PersonName().firstName("John").lastName("Doe")))
                .contentType("application/json")
                .when()
                .post(consumerBaseUrl())
                .then()
                .statusCode(200)
                .extract()
                .path("consumerId");

        assertNotNull(consumerId);
        return consumerId;
    }

    private void verifyAccountCreatedForConsumer(int consumerId) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        given()
                                .when()
                                .get(accountingBaseUrl(Integer.toString(consumerId)))
                                .then()
                                .statusCode(200));
    }

    private int createRestaurant() {
        CreateRestaurantRequest request = new CreateRestaurantRequest()
                .name(RESTAURANT_NAME)
                .address(new Address()
                        .street1("1 Main Street")
                        .street2("Unit 99")
                        .city("Oakland")
                        .state("CA")
                        .zip("94611"))
                .menu(new RestaurantMenu()
                        .addMenuItemsItem(new MenuItem()
                                .id(CHICKEN_VINDALOO_MENU_ITEM_ID)
                                .name("Chicken Vindaloo")
                                .price(priceOfChickenVindaloo.asString())));

        Integer restaurantId = given()
                .body(request)
                .contentType("application/json")
                .when()
                .post(restaurantBaseUrl())
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        assertNotNull(restaurantId);
        return restaurantId;
    }

    private void verifyRestaurantCreatedInKitchenService(int restaurantId) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        given()
                                .when()
                                .get(kitchenRestaurantBaseUrl(Integer.toString(restaurantId)))
                                .then()
                                .statusCode(200));
    }

    private void verifyRestaurantCreatedInOrderService(int restaurantId) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        given()
                                .when()
                                .get(orderRestaurantBaseUrl(Integer.toString(restaurantId)))
                                .then()
                                .statusCode(200));
    }

    private int createOrder(int consumerId, int restaurantId) {
        Integer orderId = given()
                .body(new CreateOrderRequest(consumerId, restaurantId,
                        new Address("9 Amazing View", null, "Oakland", "CA", "94612"),
                        LocalDateTime.now(),
                        Collections.singletonList(new CreateOrderRequest.LineItem(CHICKEN_VINDALOO_MENU_ITEM_ID, 5))))
                .contentType("application/json")
                .when()
                .post(orderBaseUrl())
                .then()
                .statusCode(200)
                .extract()
                .path("orderId");

        assertNotNull(orderId);
        return orderId;
    }

    private void verifyOrderAuthorized(int orderId) {
        await()
                .atMost(60, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    String state = given()
                            .when()
                            .get(orderBaseUrl(Integer.toString(orderId)))
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("orderInfo.state");
                    assertEquals("APPROVED", state);
                });
    }

    private void verifyOrderHistoryUpdated(int orderId, int consumerId, String expectedState) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    String state = given()
                            .when()
                            .get(orderHistoryBaseUrl() + "?consumerId=" + consumerId)
                            .then()
                            .statusCode(200)
                            .body("orders[0].restaurantName", equalTo(RESTAURANT_NAME))
                            .extract()
                            .path("orders[0].status");
                    assertEquals(expectedState, state);
                });
    }

    private void noteCourierAvailable() {
        courierId = System.currentTimeMillis();
        given()
                .body(new CourierAvailability(true))
                .contentType("application/json")
                .when()
                .post(deliveryServiceBaseUrl("couriers", Long.toString(courierId), "availability"))
                .then()
                .statusCode(200);
    }

    private void acceptTicket() {
        courierId = System.currentTimeMillis();
        given()
                .body(new TicketAcceptance(LocalDateTime.now().plusHours(9)))
                .contentType("application/json")
                .when()
                .post(kitchenServiceBaseUrl("tickets", Long.toString(orderId), "accept"))
                .then()
                .statusCode(200);
    }

    private void assertOrderAssignedToCourier() {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    long assignedCourier = given()
                            .when()
                            .get(deliveryServiceBaseUrl("deliveries", Long.toString(orderId)))
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("assignedCourier");
                    assertThat(assignedCourier).isGreaterThan(0);
                });
    }
}
