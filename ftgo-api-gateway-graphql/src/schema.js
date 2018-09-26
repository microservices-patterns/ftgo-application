const { makeExecutableSchema } = require("graphql-tools");

const fetch = require("node-fetch");

const gql = String.raw;

// Construct a schema, using GraphQL schema language
// TODO need order(orderId) which does API composition

const typeDefs = gql`
  type Query {
    orders(consumerId : Int!): [Order]
    order(orderId : Int!): Order
    consumer(consumerId : Int!): Consumer
  }

  type Mutation {
    createConsumer(c : ConsumerInfo) : Consumer
  }

  type Order {
    orderId: ID,
    consumerId : Int,
    consumer: Consumer
    restaurant: Restaurant
    deliveryInfo : DeliveryInfo
  }

  type Restaurant {
    id: ID
    name: String
  }

  type Consumer {
    id: ID
    firstName: String
    lastName: String
    orders: [Order]
  }

  input ConsumerInfo {
    firstName: String
    lastName: String
  }
  
  type DeliveryInfo {
    status : DeliveryStatus
    estimatedDeliveryTime : Int
    assignedCourier :String
  }
  
  enum DeliveryStatus {
    PREPARING
    READY_FOR_PICKUP
    PICKED_UP
    DELIVERED
  }

`;

function resolveOrders(_, { consumerId }, context) {
  return context.orderServiceProxy.findOrders(consumerId);
}

function resolveConsumer(_, { consumerId }, context) {
  return context.consumerServiceProxy.findConsumer(consumerId);
}

function resolveOrder(_, { orderId }, context) {
  return context.orderServiceProxy.findOrder(orderId);
}

function resolveOrderConsumer({consumerId}, args, context) {
    return context.consumerServiceProxy.findConsumer(consumerId);
}

function resolveOrderRestaurant({restaurantId}, args, context) {
    return context.restaurantServiceProxy.findRestaurant(restaurantId);
}

function resolveOrderDeliveryInfo({orderId}, args, context) {
    return context.deliveryServiceProxy.findDeliveryForOrder(orderId);
}

function resolveConsumerOrders({id, orders}, args, context) {
  return orders || context.orderServiceProxy.findOrders(id)
}

function createConsumer(_, { c: {firstName, lastName} }, context) {
  return context.consumerServiceProxy.createConsumer(firstName, lastName);
}

const resolvers = {
  Query: {
    orders: resolveOrders,
    consumer: resolveConsumer,
    order: resolveOrder
  },
  Mutation: {
    createConsumer: createConsumer
  },
  Order: {
    consumer: resolveOrderConsumer,
    restaurant: resolveOrderRestaurant,
    deliveryInfo: resolveOrderDeliveryInfo
  },
  Consumer: {
    orders: resolveConsumerOrders
  }
};

const schema = makeExecutableSchema({ typeDefs, resolvers });


module.exports = { schema };
