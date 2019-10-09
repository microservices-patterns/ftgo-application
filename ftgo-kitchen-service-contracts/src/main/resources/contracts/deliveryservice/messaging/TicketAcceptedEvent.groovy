package contracts.messaging;

org.springframework.cloud.contract.spec.Contract.make {
    label 'ticketAcceptedEvent'
    input {
        triggeredBy('ticketAcceptedEvent()')
    }

    outputMessage {
        sentTo('net.chrisrichardson.ftgo.kitchenservice.domain.Ticket')
        body(
                readyBy: $(consumer('2019-08-20T14:20:00.979'), producer(regex('20[0-9]{2}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]+')))
        )
        headers {
            header('event-aggregate-type', 'net.chrisrichardson.ftgo.kitchenservice.domain.Ticket')
            header('event-type', 'net.chrisrichardson.ftgo.kitchenservice.api.events.TicketAcceptedEvent')
            header('event-aggregate-id', '99')
        }
    }
}