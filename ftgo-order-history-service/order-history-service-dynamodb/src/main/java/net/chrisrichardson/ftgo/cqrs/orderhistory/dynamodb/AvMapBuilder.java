package net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class AvMapBuilder {

  private Map<String, AttributeValue> eav = new HashMap<>();

  public AvMapBuilder(String key, AttributeValue value) {
    eav.put(key, value);
  }

  public AvMapBuilder add(String key, String value) {
    eav.put(key, new AttributeValue(value));
    return this;
  }

  public AvMapBuilder add(String key, AttributeValue value) {
    eav.put(key, value);
    return this;
  }

  public Map<String, AttributeValue> map() {
    return eav;
  }
}
