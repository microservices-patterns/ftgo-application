import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class WaitForServices extends DefaultTask {

  private String ports;
  private String host;

  @Option(option = "ports", description = "Ports to check")
  public void setPorts(String ports) {
    this.ports = ports;
  }

  @Input
  public String getPorts() {
    return ports;
  }

  @Option(option = "host", description = "Host to check")
  public void setHost(String host) {
    this.host = host;
  }

  @Input
  public String getHost() {
    return host;
  }

  @TaskAction
  public void waitForServices() {
    System.out.println("Waiting for Services...");

    Arrays
            .stream(ports.split(" "))
            .map(Integer::parseInt)
            .forEach(this::waitForPort);

    System.out.println("All services are ready");
  }

  private void waitForPort(int port) {
    while (true) {
      try {
        connect(port);
        break;
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        System.out.println(String.format("Service with port %s is not ready, sleeping for 5 seconds", port));
        sleep();
      }
    }
  }

  private void connect(int port) throws IOException, MalformedURLException {
    String url = String.format("http://%s:%s/health", host, port);
    URL obj = new URL(url);
    URLConnection urlConnection = obj.openConnection();
    urlConnection.connect();
    System.out.println(String.format("Service with port %s is ready", port));
  }

  private void sleep() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }
}