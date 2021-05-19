package net.chrisrichardson.ftgo.consumerservice.web;

import java.util.List;

public class GetConsumersResponse {
    private List<GetConsumerResponse> consumers;

    public List<GetConsumerResponse> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<GetConsumerResponse> consumers) {
        this.consumers = consumers;
    }

    public GetConsumersResponse(List<GetConsumerResponse> consumers) {
        this.consumers = consumers;
    }
}
