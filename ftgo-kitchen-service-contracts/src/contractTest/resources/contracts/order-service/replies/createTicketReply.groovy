package contracts.replies

org.springframework.cloud.contract.spec.Contract.make {
    label 'createTicket'
    input {
        triggeredBy('createTicketReply()')
    }
    outputMessage {
        sentTo('net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        body([
                ticketId: 99
        ])
        headers {
            header('reply_type', 'net.chrisrichardson.ftgo.kitchenservice.api.CreateTicketReply')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}
