package net.chrisrichardson.ftgo.consumerservice.contracts;

import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.spring.testing.cloudcontract.EnableEventuateTramContractVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {MessagingBase.TestConfig.class})
public abstract class MessagingBase {

    private static final String SAGA_REPLY_CHANNEL = "net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply";

    @Configuration
    @EnableAutoConfiguration
    @EnableEventuateTramContractVerifier
    public static class TestConfig { }

    @Autowired
    private MessageProducer messageProducer;

    // Reply triggers - called by contract tests for saga replies
    protected void validateOrderByConsumerReply() {
        messageProducer.send(SAGA_REPLY_CHANNEL, withSuccess());
    }
}
