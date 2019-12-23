import org.gradle.api.Plugin
import org.gradle.api.Project

class FtgoJSONSchema2PojoPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def jsonSchemaSources = project.container(JSONSchemaSource)
        project.extensions.add('ftgoJsonSchema2Pojo', jsonSchemaSources)

        jsonSchemaSources.all {
            def source = delegate as JSONSchemaSource

            source.codeGen = project.task("ftgoJSONSchemaToPojoCodeGen${source.name.capitalize()}",
                    type: FtgoJSONSchemaToPojoCodeGen,
                    group: 'build',
                    description: "Code generator") as FtgoJSONSchemaToPojoCodeGen


            source.codeGen.targetDirectory = new File(project.buildDir, "generated-js2p-code-${source.name}")


            project.afterEvaluate {
                project.sourceSets[source.sourceSet].java.srcDirs += [source.codeGen.targetDirectory]
                project.tasks.compileJava.dependsOn(source.codeGen)
            }


        }
    }

}