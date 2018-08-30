import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class InitDynamoDbPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getTasks().create("initDynamoDb", InitDynamoDb.class);
  }
}
