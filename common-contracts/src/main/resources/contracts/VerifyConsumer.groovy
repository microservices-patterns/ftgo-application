package contracts;

org.springframework.cloud.contract.spec.Contract.make {
    label 'verifyConsumer'
    input {
        messageFrom('consumerService')
        messageBody([
            consumerId: 1511300065921L,
            orderId: 1,
            orderTotal: "61.70"
        ])
        messageHeaders {
        }
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