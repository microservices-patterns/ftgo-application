package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.FIELD)
public class Money implements Serializable {

  public static Money ZERO = new Money(0);

  private Double amount;

  private Money() {}

  public Money(Double amount) {
    this.amount = amount;
  }

  public Money(String s) {
    this.amount = new Double(s);
  }

  public Money(int i) {
    this.amount = new Double(i);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Money money = (Money) o;

    return new EqualsBuilder().append(amount, money.amount).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(amount).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("amount", amount).toString();
  }

  public Money add(Money delta) {
    return new Money(amount + delta.amount);
  }

  public boolean isGreaterThanOrEqual(Money other) {
    return amount.compareTo(other.amount) >= 0;
  }

  public String asString() {
    return amount.toString();
  }

  public Money multiply(int x) {
    return new Money(amount * x);
  }

  public Long asLong() {
    return multiply(100).amount.longValue();
  }
}
