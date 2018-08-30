import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class WaitForServicesPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getTasks().create("waitForServices", WaitForServices.class);
  }
}
