package net.chrisrichardson.ftgo.orderservice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MessagingStubConfiguration {
  
  private Set<String> channels = new HashSet<>();

  public MessagingStubConfiguration(String... channels) {
    this.channels = new HashSet<>(Arrays.asList(channels));
  }

  public void add(String channel) {
    channels.add(channel);
  }

  public Set<String> getChannels() {
    return channels;
  }
}
