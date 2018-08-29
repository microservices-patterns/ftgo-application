import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InitDynamoDb extends DefaultTask {

  @TaskAction
  public void initDynamoDb() {
    System.out.println("Initializing dynamodb...");

    String endPoint = System.getenv("AWS_DYNAMODB_ENDPOINT_URL");

    System.out.println("Checking if table already exists...");

    if (!checkIfTableAlreadyExist(endPoint)) {
      System.out.println("Table does not exist, creating...");
      initDb(endPoint);
    } else {
      System.out.println("Table already exists, do nothing");
    }
  }

  private boolean checkIfTableAlreadyExist(String endPoint) {
    String cmd = String.format("aws dynamodb --region us-west-1 --endpoint-url %s describe-table --table-name ftgo-order-history", endPoint == null ? "" : endPoint);
    return exec(cmd);
  }

  private void initDb(String endPoint) {
      String cmd = String.format("aws dynamodb create-table --region us-west-2 --endpoint-url %s --cli-input-json file://./dynamodblocal-init/ftgo-order-history.json", endPoint == null ? "" : endPoint);
      if (exec(cmd)) {
        System.out.println("Initialization succeed");
      } else {
        System.out.println("Initialization failed");
      }
  }

  private boolean exec(String command) {
    Process p;

    try {
      p = Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      System.out.println("command execution failed");
      throw new RuntimeException(e);
    }

    try {
      p.waitFor();
    } catch (InterruptedException e) {
      System.out.println("Waiting for command failed.");
    }

    String result;
    try {
      result = readFromStream(p.getInputStream());
    } catch (IOException e) {
      System.out.println("Result read failed");
      throw new RuntimeException(e);
    }

    if (result.isEmpty()) {
      try {
        result = readFromStream(p.getErrorStream());
        System.out.println(result);
      } catch (IOException e) {
        System.out.println("Error read failed");
      }
      return false;
    } else {
      System.out.println(result);
      return true;
    }
  }

  private String readFromStream(InputStream inputStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    StringBuilder sb = new StringBuilder();
    String line = "";
    while ((line = reader.readLine())!= null) {
      sb.append(line + "\n");
    }
    return sb.toString();
  }

}