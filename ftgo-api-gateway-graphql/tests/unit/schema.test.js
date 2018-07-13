const {graphql} = require('graphql');
const {schema} = require("../../src/schema");


const gql = String.raw;


// Construct a schema, using GraphQL schema language
// TODO need order(orderId) which does API composition

var context = {};

beforeEach(() => {
    context = {consumerServiceProxy: {}, orderServiceProxy: {}, restaurantServiceProxy: {}, deliveryServiceProxy: {}}
});

test('query consumer', () => {

    const mockFindConsumer = context.consumerServiceProxy.findConsumer = jest.fn((consumerId) => {
        return {id: consumerId}
    });

    const query = gql `
    query { consumer(consumerId:1)  { id, firstName, lastName} }
  `;

    return graphql(schema, query, null, context)
        .then((res) => {
            console.log("res=", res);
            expect(res.errors).toBe(undefined)
            expect(res.data.consumer.id).toBe('1')
        });
});

test('query consumer with variables', () => {

    const mockFindConsumer = context.consumerServiceProxy.findConsumer = jest.fn((consumerId) => {
        return {id: consumerId}
    });

    const query = gql `
    query foo($cid: Int!) {
      consumer(consumerId: $cid) {
        id    firstName    lastName    __typename  
       }
    }
  `;

    return graphql(schema, query, null, context, {cid: 1})
        .then((res) => {
            console.log("res=", res);
            expect(res.errors).toBe(undefined)
            expect(res.data.consumer.id).toBe('1')
        });
});

test('query consumer 2', () => {

    const mockFindConsumer = context.consumerServiceProxy.findConsumer = jest.fn((consumerId) => {
        return {id: consumerId}
    });

    const query = gql `
    query a1 {
      c1: consumer (consumerId:1)  { id, firstName, lastName}
      c2: consumer (consumerId:2)  { id, firstName, lastName}
      c3: consumer (consumerId:2)  { id, firstName, lastName}
    }
    query b2 {
      c4: consumer (consumerId:1)  { id, firstName, lastName}
    }
  `;

    return graphql(schema, query, null, context, {}, "a1") // can only execute one at a time!
        .then((res) => {
            console.log("res=", res);
            expect(res.errors).toBe(undefined)
            expect(res.data.c1.id).toBe('1')
            expect(res.data.c2.id).toBe('2')
        });
});

test('query consumer and orders', () => {

    // Jest .returnValue(...)?

    const mockFindConsumer = context.consumerServiceProxy.findConsumer = jest.fn((consumerId) => {
        return {id: consumerId}
    });
    const mockFindOrders = context.orderServiceProxy.findOrders = jest.fn((consumerId) => {
        return [{orderId: 99}]
    });
    const mockFindRestaurant = context.restaurantServiceProxy.findRestaurant = jest.fn((restaurantId) => {
        return {id: 101, name: 'Ajanta'}
    });

    const query = gql `
    query { consumer(consumerId:1)  { id, firstName, lastName  orders { orderId restaurant {id name }} } }
  `;

    return graphql(schema, query, null, context)
        .then((res) => {
            console.log("res=", res);
            expect(res.errors).toBe(undefined)
            expect(res.data.consumer.id).toBe('1')
            expect(res.data.consumer.orders[0].orderId).toBe('99')
            expect(res.data.consumer.orders[0].restaurant.name).toBe('Ajanta')
        });
});

test('query order', () => {

    // Jest .returnValue(...)?

    const mockFindConsumer = context.consumerServiceProxy.findConsumer = jest.fn((consumerId) => {
        return {id: consumerId}
    });
    const mockFindRestaurant = context.restaurantServiceProxy.findRestaurant = jest.fn((restaurantId) => {
        return {id: 101, name: 'Ajanta'}
    });

    const mockFindOrder = context.orderServiceProxy.findOrder = jest.fn().mockReturnValue({
        orderId: 99,
        restaurantId: 101
    });
    const mockFindDeliveryForOrder = context.deliveryServiceProxy.findDeliveryForOrder = jest.fn().mockReturnValue({status: "PICKED_UP"});

    const query = gql`
    query { order(orderId:99) { 
        orderId 
        restaurant {id name } 
        deliveryInfo {
            status
        }
     }
    } 
  `;

    return graphql(schema, query, null, context)
        .then((res) => {
            console.log("res=", res);
            expect(res.errors).toBe(undefined)
            expect(res.data.order.orderId).toBe('99')
            expect(res.data.order.restaurant.name).toBe('Ajanta')
            expect(res.data.order.deliveryInfo.status).toBe('PICKED_UP')

            expect(mockFindRestaurant).toHaveBeenLastCalledWith(101);
        });
});
