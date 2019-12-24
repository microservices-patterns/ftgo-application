import org.gradle.api.tasks.Sync

class FtgoResolveAPIDependencies extends Sync {

    FtgoResolveAPIDependencies() {


        from {
            project.configurations.ftgoApiSpecification.resolve().collect {
                project.zipTree(it)
            }
        }
        include("**/*.json")
        exclude("**/META-INF/**")
        into(project.ext.ftgoApiSpecsDir )

        includeEmptyDirs = false

    }

}
