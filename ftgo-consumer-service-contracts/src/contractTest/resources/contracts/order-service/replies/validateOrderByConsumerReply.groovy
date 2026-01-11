package contracts.replies

org.springframework.cloud.contract.spec.Contract.make {
    label 'verifyConsumer'
    input {
        triggeredBy('validateOrderByConsumerReply()')
    }
    outputMessage {
        sentTo('net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        body('''{}''')
        headers {
            header('reply_type', 'io.eventuate.tram.commands.common.Success')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}