const fetch = require("node-fetch");

class OrderServiceProxy {

    constructor(options) {
        this.orderService = `${options.baseUrl}/orders`;
    }

    findOrders(consumerId) {
        return fetch(`${this.orderService}?consumerId=${consumerId}`)
            .then(res => res.json())
            .then(data => {
                // Sometimes, there are no upcoming events
                console.log("orders=", data);
                const x = data.orders.map((order) => Object.assign({consumerId}, order));
                console.log("orders=", x);
                return x;
            });
    }

}

module.exports = {OrderServiceProxy};
