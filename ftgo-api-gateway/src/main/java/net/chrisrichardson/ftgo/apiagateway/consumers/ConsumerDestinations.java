package net.chrisrichardson.ftgo.apiagateway.consumers;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "consumer.destinations")
public class ConsumerDestinations {

  @NotNull
  private String consumerServiceUrl;

  public String getConsumerServiceUrl() {
    return consumerServiceUrl;
  }

  public void setConsumerServiceUrl(String consumerServiceUrl) {
    this.consumerServiceUrl = consumerServiceUrl;
  }
}
