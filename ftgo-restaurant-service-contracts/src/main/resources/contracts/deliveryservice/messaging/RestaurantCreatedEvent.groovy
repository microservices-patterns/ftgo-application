package contracts.deliveryservice.messaging;

org.springframework.cloud.contract.spec.Contract.make {
    label 'restaurantCreatedEvent'
    input {
        triggeredBy('restaurantCreated()')
    }

    outputMessage {
        sentTo('net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant')
        body('''{"address":{ "street1" : "1 Main Street", "street2" : "Unit 99", "city" : "Oakland", "state" : "CA", "zip" : "94611", }''')
        headers {
            header('event-aggregate-type', 'net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant')
            header('event-type', 'net.chrisrichardson.ftgo.restaurantservice.events.RestaurantCreated')
            header('event-aggregate-id', '99') // Matches RestaurantDetailsMother.RESTAURANT_ID
        }
    }
}