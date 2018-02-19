package net.chrisrichardson.ftgo.orderservice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MessageTrackerConfiguration {
  private Set<String> channels;

  public MessageTrackerConfiguration(String... channels) {
    this.channels = new HashSet<>(Arrays.asList(channels));
  }

  public MessageTrackerConfiguration(Set<String> channels) {
    this.channels = channels;
  }

  public void add(String channel) {
    channels.add(channel);
  }


  public Set<String> getChannels() {
    return channels;
  }
}
