package net.chrisrichardson.ftgo.cqrs.orderhistory.dynamodb;

import org.apache.commons.lang.StringUtils;

import java.util.Optional;

public class Expressions {

  static String and(String s1, Optional<String> s2) {
    return s2.map(x -> and(s1, x)).orElse(s1);
  }

  static String and(String s1, String s2) {
    if (StringUtils.isBlank(s1))
      return s2;
    if (StringUtils.isBlank(s2))
      return s1;
    return String.format("(%s) AND (%s)", s1, s2);
  }

  static String or(String s1, String s2) {
    if (StringUtils.isBlank(s1))
      return s2;
    if (StringUtils.isBlank(s2))
      return s1;
    return String.format("(%s) AND (%s)", s1, s2);
  }
}
