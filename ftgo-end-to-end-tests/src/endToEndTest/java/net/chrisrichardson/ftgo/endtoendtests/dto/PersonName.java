package net.chrisrichardson.ftgo.endtoendtests.dto;

public class PersonName {
    private String firstName;
    private String lastName;

    public PersonName() {
    }

    public PersonName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public PersonName firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonName lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
