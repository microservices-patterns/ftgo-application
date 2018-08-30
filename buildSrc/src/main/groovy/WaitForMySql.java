import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WaitForMySql extends DefaultTask {

  @TaskAction
  public void waitForMySql() {
    System.out.println("Waiting for MySql...");

    loadDriver();

    waitForConnection();
  }

  private void loadDriver() {
    try {
      System.out.println("Trying to initialize driver");

      String datasourceDriverClassName = System.getenv("SPRING_DATASOURCE_DRIVER_CLASS_NAME");
      Class.forName(datasourceDriverClassName);

      System.out.println("Initialization succeed");
    } catch (ClassNotFoundException e) {
      System.out.println("Initialization failed");

      throw new RuntimeException(e);
    }
  }

  private void waitForConnection() {
    while (true) {
      Connection connection = null;
      try {
        System.out.println("Trying to connect...");

        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        String datasourceUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
        String datasourcePassword = System.getenv("SPRING_DATASOURCE_PASSWORD");

        connection = DriverManager.getConnection(datasourceUrl, datasourceUsername, datasourcePassword);

        System.out.println("Connection succeed");

        break;
      } catch (SQLException e) {
        System.out.println("Connection failed");

        sleep();
      } finally {
        if (connection != null) {
          closeConnection(connection);
        }
      }
    }
  }

  private void closeConnection(Connection connection) {
    try {
      System.out.println("Trying to close connection");

      connection.close();

      System.out.println("Connection closed");
    } catch (SQLException e) {
      System.out.println("Failed to close connection, see stack trace:");

      e.printStackTrace();
    }
  }

  private void sleep() {
    System.out.println("sleeping for 5 seconds...");

    try {
      Thread.sleep(5000);
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }
}