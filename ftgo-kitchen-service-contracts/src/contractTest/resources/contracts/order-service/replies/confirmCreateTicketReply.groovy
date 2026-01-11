package contracts.replies

org.springframework.cloud.contract.spec.Contract.make {
    label 'confirmCreateTicket'
    input {
        triggeredBy('confirmCreateTicketReply()')
    }
    outputMessage {
        sentTo('net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        headers {
            header('reply_type', 'io.eventuate.tram.commands.common.Success')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}
