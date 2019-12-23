import groovy.transform.ToString
import org.gradle.api.file.FileCollection

@ToString(includes = 'name', includePackage = false)
class JSONSchemaSource {

    final String name

    def JSONSchemaSource(String name) {
        this.name = name
    }

    FtgoJSONSchemaToPojoCodeGen codeGen

    String sourceSet = "main"

    void setSource(FileCollection source) {
        this.codeGen.source = source
    }

    void setTargetPackage(String targetPackage) {
        this.codeGen.targetPackage = targetPackage
    }

    void setIncludeAdditionalProperties(boolean includeAdditionalProperties) {
        this.codeGen.includeAdditionalProperties = includeAdditionalProperties
    }

    void setGenerateBuilders(boolean generateBuilders) {
        this.codeGen.generateBuilders = generateBuilders
    }

    void setUseLongIntegers(boolean useLongIntegers) {
        this.codeGen.useLongIntegers = useLongIntegers
    }

    File getTargetDirectory() {
        return this.codeGen.targetDirectory
    }

    void setSourceSet(String sourceSet) {
        this.sourceSet = sourceSet
    }
}
