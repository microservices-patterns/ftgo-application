const fetch = require("node-fetch");
const DataLoader = require('dataloader');


class ConsumerServiceProxy {

    constructor(options) {
        console.log("ConsumerServiceProxy constructor", options, DataLoader);
        this.dataLoader = new DataLoader(keys => this.batchFindConsumers(keys));
        this.consumerService = `${options.baseUrl}/consumers`;
    }

    findConsumer(consumerId) {
        console.log("findConsumer")
        return this.dataLoader.load(consumerId);
    }

    findConsumerInternal(consumerId) {
        console.log("findConsumerInternal")
        return fetch(`${this.consumerService}/${consumerId}`)
            .then(response => {
                console.log("response=", response.status);
                if (response.status == 200) {
                    return response.json().then(body => {
                        console.log("response=", body);
                        return Object.assign({
                            id: consumerId,
                            firstName: body.name.firstName,
                            lastName: body.name.lastName
                        }, body);
                    })
                } else
                    return Promise.reject(new Error("cannot found consumer for id" + consumerId))
            });
    }

    createConsumer(firstName, lastName) {
        console.log("createConsumer")
        return fetch(this.consumerService, {
            method: 'POST',
            body: JSON.stringify({name: {firstName, lastName}}),
            headers: {"Content-Type": "application/json"}
        })
            .then(r => r.json())
            .then(({consumerId}) => {
                console.log("consumerId=", consumerId);
                return {id: consumerId, orders: []};
            })
    }

    batchFindConsumers(keys) {
        console.log("keys=", keys);
        return Promise.all(keys.map(k => this.findConsumerInternal(k)));
    }

}

module.exports = {ConsumerServiceProxy};
