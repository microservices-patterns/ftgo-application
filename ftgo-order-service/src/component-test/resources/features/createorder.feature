Feature: Create order

  As a consumer of the Order Service
  I should be able to create an order

  Scenario: Order authorized
    Given A valid consumer
    Given using a valid credit card
    Given the restaurant is accepting orders
    When I place an order for Chicken Vindaloo at Ajanta
    Then the order should be AUTHORIZED
    And an OrderAuthorized event should be published

  Scenario: Order rejected due to expired credit card
    Given A valid consumer
    Given using an expired credit card
    Given the restaurant is accepting orders
    When I place an order for Chicken Vindaloo at Ajanta
    Then the order should be REJECTED
    And an OrderRejected event should be published
