package net.chrisrichardson.ftgo.endtoendtests.dto;

public class Address {
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zip;

    public Address() {
    }

    public Address(String street1, String street2, String city, String state, String zip) {
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet1() {
        return street1;
    }

    public Address street1(String street1) {
        this.street1 = street1;
        return this;
    }

    public String getStreet2() {
        return street2;
    }

    public Address street2(String street2) {
        this.street2 = street2;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public Address state(String state) {
        this.state = state;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public Address zip(String zip) {
        this.zip = zip;
        return this;
    }
}
