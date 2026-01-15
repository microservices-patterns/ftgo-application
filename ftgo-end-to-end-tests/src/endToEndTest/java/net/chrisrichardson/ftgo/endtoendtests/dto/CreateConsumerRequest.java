package net.chrisrichardson.ftgo.endtoendtests.dto;

public class CreateConsumerRequest {
    private PersonName name;

    public CreateConsumerRequest() {
    }

    public PersonName getName() {
        return name;
    }

    public CreateConsumerRequest name(PersonName name) {
        this.name = name;
        return this;
    }
}
