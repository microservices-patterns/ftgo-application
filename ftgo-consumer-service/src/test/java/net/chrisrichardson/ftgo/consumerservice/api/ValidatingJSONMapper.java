package net.chrisrichardson.ftgo.consumerservice.api;

import io.eventuate.common.json.mapper.JSonMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

public class ValidatingJSONMapper {

  private Schema schema;

  public ValidatingJSONMapper(Schema schema) {
    this.schema = schema;
  }

  public static ValidatingJSONMapper forSchema(String schemaPath) {
    Schema schema;
    try (InputStream inputStream = ValidatingJSONMapper.class.getResourceAsStream(schemaPath)) {
      JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
      schema = SchemaLoader.load(rawSchema);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ValidatingJSONMapper(schema);
  }

  public void validate(JSONObject jsonObject) {
    try {
      schema.validate(jsonObject);
    } catch (ValidationException e) {
      e.getErrorMessage();
      fail("Schema validation failed: " + String.join(",", e.getAllMessages()));
    }

  }

  <T> T fromJSON(JSONObject jsonObject, Class<T> clasz) {
    validate(jsonObject);
    return JSonMapper.fromJson(jsonObject.toString(), clasz);
  }
}
