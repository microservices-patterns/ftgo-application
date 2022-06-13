import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class FtgoServicePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.apply(plugin: 'org.springframework.boot')
    	project.apply(plugin: "io.spring.dependency-management")

        project.dependencyManagement {
            imports {
                mavenBom "org.springframework.cloud:spring-cloud-contract-dependencies:${project.ext.springCloudContractDependenciesVersion}"
                mavenBom "org.springframework.cloud:spring-cloud-sleuth:${project.ext.springCloudSleuthVersion}"
                mavenBom "io.eventuate.platform:eventuate-platform-dependencies:${project.ext.eventuatePlatformVersion}"
            }
        }

        project.configurations.all {
            exclude group: 'org.apache.logging.log4j'
            exclude group: 'log4j'
        }

        project.dependencies {
            compile 'org.springframework.cloud:spring-cloud-starter-sleuth'
            compile 'org.springframework.cloud:spring-cloud-starter-zipkin'
            compile "io.zipkin.brave:brave-bom:4.17.1"

            // Temporarily disable
            //compile "io.eventuate.tram.core:eventuate-tram-spring-cloud-sleuth-integration"

            implementation(platform("io.eventuate.platform:eventuate-platform-dependencies:${project.ext.eventuatePlatformVersion}"))
        }

    }
}
