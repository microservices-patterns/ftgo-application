package net.chrisrichardson.ftgo.common;

import io.eventuate.javaclient.commonimpl.JSonMapper;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoneySerializationTest {

  @BeforeClass
  public static void initialize() {
    CommonJsonMapperInitializer.registerMoneyModule();
  }


  public static class MoneyContainer {
    private Money amount;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;

      if (o == null || getClass() != o.getClass()) return false;

      MoneyContainer that = (MoneyContainer) o;

      return new EqualsBuilder()
              .append(amount, that.amount)
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

    public Money getAmount() {
      return amount;
    }

    public void setAmount(Money amount) {
      this.amount = amount;
    }

    public MoneyContainer() {

    }

    public MoneyContainer(Money amount) {

      this.amount = amount;
    }
  }

  @Test
  public void shouldSer() {
    Money amount = new Money("12.34");
    MoneyContainer mc = new MoneyContainer(amount);
    assertEquals("{\"amount\":\"12.34\"}", JSonMapper.toJson(mc));
  }

  @Test
  public void shouldDe() {
    Money amount = new Money("12.34");
    MoneyContainer mc = new MoneyContainer(amount);
    assertEquals(mc, JSonMapper.fromJson("{\"amount\":\"12.34\"}", MoneyContainer.class));
  }


}