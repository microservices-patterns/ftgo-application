package net.chrisrichardson.ftgo.consumerservice.web;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;
import com.atlassian.oai.validator.springmvc.SpringMVCLevelResolverFactory;
import io.eventuate.common.json.mapper.JSonMapper;
import net.chrisrichardson.ftgo.common.CommonJsonMapperInitializer;
import net.chrisrichardson.ftgo.common.PersonName;
import net.chrisrichardson.ftgo.consumerservice.domain.Consumer;
import net.chrisrichardson.ftgo.consumerservice.domain.ConsumerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.mock;

public class ConsumerControllerTest {

  private ConsumerController consumerController;
  private ConsumerService consumerService;
  private Consumer consumer;

  @Before
  public void setUp() {
    consumerService = mock(ConsumerService.class);
    consumerController = new ConsumerController(consumerService);
    consumer = new Consumer(new PersonName("x", "y"));
  }

  @Test
  public void shouldGetConsumer() throws IOException {
    Mockito.when(consumerService.findById(1)).thenReturn(Optional.of(consumer));
    given().
            standaloneSetup(configureControllers(consumerController)).
            when().
            get("/consumers/1").
            then().
            statusCode(200)
    ;

  }

  private StandaloneMockMvcBuilder configureControllers(Object... controllers) throws IOException {
    CommonJsonMapperInitializer.registerMoneyModule();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);

    ClassPathResource swaggerResource = new ClassPathResource("ftgo-consumer-service-swagger.json");

    String specification = StreamUtils.copyToString(swaggerResource.getInputStream(), Charset.forName("UTF-8"));

    OpenApiInteractionValidator validator = OpenApiInteractionValidator
            .createForInlineApiSpecification(specification)
            .withLevelResolver(SpringMVCLevelResolverFactory.create())
            .build();

    OpenApiValidationInterceptor validationInterceptor = new OpenApiValidationInterceptor(validator);

    OpenApiValidationFilter openApiValidationFilter = new OpenApiValidationFilter(
            true, // enable request validation
            true  // enable response validation
    );

    return MockMvcBuilders.standaloneSetup(controllers)
            .setMessageConverters(converter)
            .addFilters(openApiValidationFilter)
            .addInterceptors(validationInterceptor);
  }

}