package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmCreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrder;
import net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrderReply;
import net.chrisrichardson.ftgo.restaurantorderservice.api.RestaurantOrderServiceChannels;

public class RestaurantOrderServiceProxy {

  public final CommandEndpoint<CreateRestaurantOrder> create = CommandEndpointBuilder
          .forCommand(CreateRestaurantOrder.class)
          .withChannel(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
          .withReply(CreateRestaurantOrderReply.class)
          .build();

  public final CommandEndpoint<ConfirmCreateRestaurantOrder> confirmCreate = CommandEndpointBuilder
          .forCommand(ConfirmCreateRestaurantOrder.class)
          .withChannel(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
          .withReply(Success.class)
          .build();

}