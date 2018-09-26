import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class WaitForMySqlPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getTasks().create("waitForMySql", WaitForMySql.class);
  }
}
