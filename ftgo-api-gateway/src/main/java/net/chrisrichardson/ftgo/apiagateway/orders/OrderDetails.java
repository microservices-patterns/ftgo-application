package net.chrisrichardson.ftgo.apiagateway.orders;

import net.chrisrichardson.ftgo.apiagateway.proxies.BillInfo;
import net.chrisrichardson.ftgo.apiagateway.proxies.DeliveryInfo;
import net.chrisrichardson.ftgo.apiagateway.proxies.OrderInfo;
import net.chrisrichardson.ftgo.apiagateway.proxies.TicketInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import reactor.util.function.Tuple4;

import java.util.Optional;

public class OrderDetails {

  private OrderInfo orderInfo;

  public OrderDetails() {
  }

  public OrderDetails(OrderInfo orderInfo) {
    this.orderInfo = orderInfo;
  }

  public OrderDetails(OrderInfo orderInfo,
                      Optional<TicketInfo> ticketInfo,
                      Optional<DeliveryInfo> deliveryInfo,
                      Optional<BillInfo> billInfo) {
    this(orderInfo);
    System.out.println("FIXME");
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }


  public OrderInfo getOrderInfo() {
    return orderInfo;
  }

  public void setOrderInfo(OrderInfo orderInfo) {
    this.orderInfo = orderInfo;
  }


  public static OrderDetails makeOrderDetails(Tuple4<OrderInfo, Optional<TicketInfo>, Optional<DeliveryInfo>, Optional<BillInfo>> info) {
    return new OrderDetails(info.getT1(), info.getT2(), info.getT3(), info.getT4());
  }
}
