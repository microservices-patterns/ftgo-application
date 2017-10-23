package net.chrisrichardson.ftgo.restaurantorderservice.domain;

import io.eventuate.tram.commands.common.Command;

public class VerifyRestaurantCommand implements Command {

  private Long orderId;
}
