package deliveryservice.messaging;

org.springframework.cloud.contract.spec.Contract.make {
    label 'orderCreatedForDeliveryService'
    input {
        triggeredBy('orderCreatedEvent()')
    }

    outputMessage {
        sentTo('net.chrisrichardson.ftgo.orderservice.domain.Order')
        body('''{"orderDetails":{"lineItems":[{"quantity":5,"menuItemId":"1","name":"Chicken Vindaloo","price":"12.34","total":"61.70"}],"orderTotal":"61.70","restaurantId":1, "consumerId":1511300065921}, "deliveryAddress":{ "street1" : "9 Amazing View", "city" : "Oakland", "state" : "CA", "zip" : "94612", }, "restaurantName" : "Ajanta"}''')
        headers {
            header('event-aggregate-type', 'net.chrisrichardson.ftgo.orderservice.domain.Order')
            header('event-type', 'net.chrisrichardson.ftgo.orderservice.api.events.OrderCreatedEvent')
            header('event-aggregate-id', '99') // Matches OrderDetailsMother.ORDER_ID
        }
    }
}