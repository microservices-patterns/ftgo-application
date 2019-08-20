package contracts.messaging;

org.springframework.cloud.contract.spec.Contract.make {
    label 'restaurantCreatedEvent'
    input {
        triggeredBy('restaurantCreated()')
    }

    outputMessage {
        sentTo('net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant')
        body('''{"restaurantDetails":{"lineItems":[{"quantity":5,"menuItemId":"1","name":"Chicken Vindaloo","price":"12.34","total":"61.70"}],"restaurantTotal":"61.70","restaurantId":1, "consumerId":1511300065921}, "restaurantName" : "Ajanta"}''')
        headers {
            header('event-aggregate-type', 'net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant')
            header('event-type', 'net.chrisrichardson.ftgo.restaurantservice.api.events.RestaurantCreatedEvent')
            header('event-aggregate-id', '99') // Matches RestaurantDetailsMother.RESTAURANT_ID
        }
    }
}