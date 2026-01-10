package net.chrisrichardson.ftgo.orderservice;

public class TramCommandsAndEventsIntegrationData {

  private long now = System.currentTimeMillis();
  private String commandDispatcherId = "command-dispatcher-" + now;
  private String eventDispatcherId  = "event-dispatcher-" + now;

  private String consumerServiceCommandChannel = "consumerServiceCommandChannel-" + now;
  private String consumerAggregateDestination = "consumerAggregateDestination-" + now;

  private String restaurantServiceCommandChannel = "restaurantServiceCommandChannel-" + now;
  private String restaurantAggregateDestination = "restaurantAggregateDestination-" + now;
  private String acccountServiceCommandChannel  = "acccountServiceCommandChannel-" + now;
  private String acccountAggregateDestination  = "acccountAggregateDestination-" + now;

  public String getRestaurantServiceCommandChannel() {
    return restaurantServiceCommandChannel;
  }

  public String getConsumerAggregateDestination() {
    return consumerAggregateDestination;
  }
  public String getConsumerServiceCommandChannel() {
    return consumerServiceCommandChannel;
  }


  public String getCommandDispatcherId() {
    return commandDispatcherId;
  }


  public String getEventDispatcherId() {
    return eventDispatcherId;
  }

  public String getRestaurantAggregateDestination() {
    return restaurantAggregateDestination;
  }

  public String getAcccountServiceCommandChannel() {
    return acccountServiceCommandChannel;
  }

  public String getAcccountAggregateDestination() {
    return acccountAggregateDestination;
  }
}
