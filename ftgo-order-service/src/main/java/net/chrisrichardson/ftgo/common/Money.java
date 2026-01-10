package net.chrisrichardson.ftgo.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;

//@Embeddable
//@Access(AccessType.FIELD)
public class Money {

  public static Money ZERO = new Money(0);

  private BigDecimal amount;

  private Money() {
  }

  public Money(BigDecimal amount) {
    this.amount = amount;
  }

  public Money(String s) {
    this.amount = new BigDecimal(s);
  }

  public Money(int i) {
    this.amount = new BigDecimal(i);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Money money = (Money) o;

    return new EqualsBuilder()
            .append(amount, money.amount)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(amount)
            .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .append("amount", amount)
            .toString();
  }


  public Money add(Money delta) {
    return new Money(amount.add(delta.amount));
  }

  public boolean isGreaterThanOrEqual(Money other) {
    return amount.compareTo(other.amount) >= 0;
  }

  public String asString() {
    return amount.toPlainString();
  }

  public Money multiply(int x) {
    return new Money(amount.multiply(new BigDecimal(x)));
  }

  public Long asLong() {
    return multiply(100).amount.longValue();
  }
}
