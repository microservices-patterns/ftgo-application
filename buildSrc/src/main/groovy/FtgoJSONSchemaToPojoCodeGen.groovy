import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.GenerationConfig
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.DefaultGenerationConfig

class FtgoJSONSchemaToPojoCodeGen  extends DefaultTask {

    FileCollection source
    String targetPackage;
    boolean includeAdditionalProperties;
    boolean generateBuilders
    boolean useLongIntegers
    File targetDirectory

    @TaskAction
    def generate() {
        GenerationConfig configuration = new DefaultGenerationConfig() {

            @Override
            Iterator<URL> getSource() {
                FtgoJSONSchemaToPojoCodeGen.this.source.collect { it.toURL()}.iterator()
            }

            @Override
            String getTargetPackage() {
                FtgoJSONSchemaToPojoCodeGen.this.targetPackage
            }

            @Override
            boolean isIncludeAdditionalProperties() {
                FtgoJSONSchemaToPojoCodeGen.this.includeAdditionalProperties
            }

            @Override
            boolean isGenerateBuilders() {
                FtgoJSONSchemaToPojoCodeGen.this.generateBuilders
            }

            @Override
            boolean isUseLongIntegers() {
                FtgoJSONSchemaToPojoCodeGen.this.useLongIntegers
            }

            @Override
            File getTargetDirectory() {
                return FtgoJSONSchemaToPojoCodeGen.this.targetDirectory
            }
        }


        Jsonschema2Pojo.generate(configuration)
    }


}
