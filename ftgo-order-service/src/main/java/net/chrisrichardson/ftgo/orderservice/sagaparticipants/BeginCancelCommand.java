package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class BeginCancelCommand extends OrderCommand {

  private BeginCancelCommand() {
  }

  public BeginCancelCommand(long orderId) {
    super(orderId);
  }
}
