Feature: Create revise and cancel

  As a consumer of the Order Service
  I should be able to create, revise and cancel an order

  Scenario: Order authorized
    Given A valid consumer
    Given using a valid credit card
    Given the restaurant is accepting orders
    When I place an order for Chicken Vindaloo at Ajanta
    Then the order should be APPROVED
    And when I revise the order
    Then it should be revised
    And when I cancel the order
    Then the order should be CANCELED
