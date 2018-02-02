package contracts;

org.springframework.cloud.contract.spec.Contract.make {
    label 'createRestaurantOrder'
    input {
        messageFrom('restaurantOrderService')
        messageBody('''{"orderId":99,"restaurantId":1,"restaurantOrderDetails":{"lineItems":[{"quantity":5,"menuItemId":"1","name":"Chicken Vindaloo"}]}}''')
        messageHeaders {
            header('command_type','net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrder')
            header('command_saga_type','net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga')
            header('command_saga_id',$(consumer(regex('[0-9a-f]{16}-[0-9a-f]{16}'))))
            header('command_reply_to', 'net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        }
    }
    outputMessage {
        sentTo('net.chrisrichardson.ftgo.orderservice.sagas.createorder.CreateOrderSaga-reply')
        body([
                restaurantOrderId: 99
        ])
        headers {
            header('reply_type', 'net.chrisrichardson.ftgo.restaurantorderservice.api.CreateRestaurantOrderReply')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}