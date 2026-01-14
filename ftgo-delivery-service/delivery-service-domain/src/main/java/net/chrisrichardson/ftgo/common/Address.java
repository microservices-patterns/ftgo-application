package net.chrisrichardson.ftgo.common;

import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class Address {

  private String street1;
  private String street2;
  private String city;
  private String state;
  private String zip;

  private Address() {
  }

  public Address(String street1, String street2, String city, String state, String zip) {
    this.street1 = street1;
    this.street2 = street2;
    this.city = city;
    this.state = state;
    this.zip = zip;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return new EqualsBuilder()
            .append(street1, address.street1)
            .append(street2, address.street2)
            .append(city, address.city)
            .append(state, address.state)
            .append(zip, address.zip)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(street1)
            .append(street2)
            .append(city)
            .append(state)
            .append(zip)
            .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .append("street1", street1)
            .append("street2", street2)
            .append("city", city)
            .append("state", state)
            .append("zip", zip)
            .toString();
  }

  public String getStreet1() {
    return street1;
  }

  public void setStreet1(String street1) {
    this.street1 = street1;
  }

  public String getStreet2() {
    return street2;
  }

  public void setStreet2(String street2) {
    this.street2 = street2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }
}
