package contracts;

org.springframework.cloud.contract.spec.Contract.make {
    label 'confirmCreateRestaurantOrder'
    input {
        messageFrom('restaurantOrderService')
        messageBody('''{"restaurantOrderId":1}''')
        messageHeaders {
            header('command_type','net.chrisrichardson.ftgo.restaurantorderservice.api.ConfirmCreateRestaurantOrder')
            header('command_saga_type','net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga')
            header('command_saga_id',$(consumer(regex('[0-9a-f]{16}-[0-9a-f]{16}'))))
            header('command_reply_to', 'net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        }
    }
    outputMessage {
        sentTo('net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        headers {
            header('reply_type', 'io.eventuate.tram.commands.common.Success')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}