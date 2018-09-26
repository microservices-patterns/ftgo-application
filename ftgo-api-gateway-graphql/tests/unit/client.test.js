const {FtgoGraphQLClient} = require("../common/ftgo-graphql-client.js");
const {graphql} = require('graphql');
const {schema} = require("../../src/schema");

const{ SchemaLink } = require('apollo-link-schema');
const {ApolloClient} = require("apollo-client");
const {InMemoryCache} = require('apollo-cache-inmemory');


var context = {};

beforeEach(() => {
    context = {consumerServiceProxy: {}, orderServiceProxy: {}, restaurantServiceProxy: {}, deliveryServiceProxy: {}}
});


test('findConsumerWithOrders', () => {

    const mockFindConsumer = context.consumerServiceProxy.findConsumer = jest.fn((consumerId) => {
        return {id: consumerId}
    });

    const mockFindOrders = context.orderServiceProxy.findOrders = jest.fn((consumerId) => {
        return [{orderId: 99}]
    });

    const mockFindRestaurant = context.restaurantServiceProxy.findRestaurant = jest.fn((restaurantId) => {
        return {id: 101, name: 'Ajanta'}
    });

    const link = new SchemaLink({ schema: schema, context });
    const cache = new InMemoryCache();

    const c = new ApolloClient({
        link,
        cache,
    });

    const client = new FtgoGraphQLClient({client: c});

    return client.findConsumerWithOrders("1")
        .then(result => {
            expect(result.errors).toBe(undefined);
            expect(result.data.consumer.id).toBe('1');
        }).then(r => client.findConsumerWithOrders("1"));

});