package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class ApproveOrderCommand extends OrderCommand {

  private ApproveOrderCommand() {
  }

  public ApproveOrderCommand(long orderId) {
    super(orderId);
  }
}
