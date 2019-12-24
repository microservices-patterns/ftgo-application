import org.gradle.api.Plugin
import org.gradle.api.Project

class FtgoApiDependencyResolverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.ext.ftgoApiSpecsDir = "${project.buildDir}/ftgo-api-specs"

        def c = project.configurations.create('ftgoApiSpecification')

        project.configurations.implementation.extendsFrom(c)

        project.task("ftgoResolveAPIDependencies",
                type: FtgoResolveAPIDependencies,
                group: 'build setup',
                description: "fetch API dependencies")
    }

}