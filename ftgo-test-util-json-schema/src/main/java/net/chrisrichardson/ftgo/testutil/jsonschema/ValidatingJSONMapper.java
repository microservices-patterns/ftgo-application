package net.chrisrichardson.ftgo.testutil.jsonschema;

import io.eventuate.common.json.mapper.JSonMapper;
import org.apache.commons.lang.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;

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
      if (inputStream == null)
        fail("Can't find schema: " + schemaPath);
      JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
      schema = SchemaLoader.load(rawSchema, new SchemaClient() {
        @Override
        public InputStream get(String url) {
          String path = StringUtils.substringBeforeLast(schemaPath, "/") + "/" + url;
          InputStream is = ValidatingJSONMapper.class.getResourceAsStream(path);
          Assert.assertNotNull(path, is);
          return is;
        }
      });
    } catch (IOException | JSONException e) {
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
  public void validate(String jsonObject) {
    JSONObject jo = new JSONObject(new JSONTokener(jsonObject));
    validate(jo);
  }

  public <T> T fromJSON(JSONObject jsonObject, Class<T> clasz) {
    validate(jsonObject);
    return JSonMapper.fromJson(jsonObject.toString(), clasz);
  }

  public String toJSON(Object object) {
    String json = JSonMapper.toJson(object);
    validate(json);
    return json;
  }
}
