import org.gradle.api.Plugin
import org.gradle.api.Project

class FtgoOpenApiCodeGenPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply("org.hidetake.swagger.generator")
        project.apply(plugin: FtgoApiDependencyResolverPlugin)

        project.afterEvaluate {
            project.swaggerSources.each {
                def name = it.name
                project.swaggerSources[name].code.dependsOn(project.tasks.getByPath("ftgoResolveAPIDependencies"))
                project.compileJava.dependsOn(project.swaggerSources[name].code)
                project.sourceSets.main.java.srcDir "${project.swaggerSources[name].code.outputDir}/src/main/java"
                project.sourceSets.main.resources.srcDir "${project.swaggerSources[name].code.outputDir}/src/main/resources"
            }
        }

    }
}
