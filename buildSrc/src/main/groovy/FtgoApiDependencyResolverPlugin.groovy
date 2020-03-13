import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency

class FtgoApiDependencyResolverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.ext.ftgoApiSpecsDir = "${project.buildDir}/ftgo-api-specs"

        def c = project.configurations.create('ftgoApiSpecification')

        project.configurations.implementation.extendsFrom(c)

        def resolveTask = project.task("ftgoResolveAPIDependencies",
                type: FtgoResolveAPIDependencies,
                group: 'build setup',
                description: "fetch API dependencies")

        project.afterEvaluate {
            project.configurations.ftgoApiSpecification.allDependencies.each {
                if (it instanceof DefaultProjectDependency) {
                    def buildTask = it.dependencyProject.tasks.getByPath("build")
                    resolveTask.dependsOn(buildTask)
                }
            }
        }
    }

}