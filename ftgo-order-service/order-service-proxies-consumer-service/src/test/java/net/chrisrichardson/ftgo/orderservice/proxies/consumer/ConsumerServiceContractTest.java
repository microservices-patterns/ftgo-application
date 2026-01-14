package net.chrisrichardson.ftgo.orderservice.proxies.consumer;

import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
        ids = "net.chrisrichardson.ftgo:ftgo-consumer-service:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.REMOTE
)
@DirtiesContext
public class ConsumerServiceContractTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfiguration {
    }

    @Autowired
    private StubFinder stubFinder;

    @Test
    public void shouldReceiveVerifyConsumerReply() {
        stubFinder.trigger("verifyConsumer");
    }
}
