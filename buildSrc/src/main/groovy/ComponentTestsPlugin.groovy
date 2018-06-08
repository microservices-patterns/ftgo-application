import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class ComponentTestsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
    
    	project.apply(plugin: 'eclipse')
    	
        project.sourceSets {
            componentTest {
                java {
                    compileClasspath += main.output + test.output
                    runtimeClasspath += main.output + test.output
                    srcDir project.file('src/component-test/java')
                }
                resources.srcDir project.file('src/component-test/resources')
            }
        }

        project.configurations {
            componentTestCompile.extendsFrom testCompile
            componentTestRuntime.extendsFrom testRuntime
        }

		project.eclipse.classpath.plusConfigurations << project.configurations.componentTestCompile

        project.task("componentTest", type: Test) {
            testClassesDir = project.sourceSets.componentTest.output.classesDir
            classpath = project.sourceSets.componentTest.runtimeClasspath
        }

        project.tasks.withType(Test) {
            reports.html.destination = project.file("${project.reporting.baseDir}/${name}")
        }
    }
}
