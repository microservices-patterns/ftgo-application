package net.chrisrichardson.ftgo.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Money money) {
        if (money == null) {
            return null;
        }
        return new BigDecimal(money.asString());
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        if (dbData == null) {
            return null;
        }
        return new Money(dbData);
    }
}
