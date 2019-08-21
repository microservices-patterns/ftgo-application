package contracts.messaging;

org.springframework.cloud.contract.spec.Contract.make {
    label 'createTicket'
    input {
        messageFrom('kitchenService')
        messageBody('''{"orderId":99,"restaurantId":1,"ticketDetails":{"lineItems":[{"quantity":5,"menuItemId":"1","name":"Chicken Vindaloo"}]}}''')
        messageHeaders {
            header('command_type','net.chrisrichardson.ftgo.kitchenservice.api.CreateTicket')
            header('command_saga_type','net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga')
            header('command_saga_id',$(consumer(regex('[0-9a-f]{16}-[0-9a-f]{16}'))))
            header('command_reply_to', 'net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        }
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