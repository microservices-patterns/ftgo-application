const fetch = require("node-fetch");

const {ApolloClient} = require("apollo-client");
const {createHttpLink} = require('apollo-link-http');
const {InMemoryCache} = require('apollo-cache-inmemory');
const gql = require('graphql-tag');
// const{ createPersistedQueryLink } = require("apollo-link-persisted-queries");


class FtgoGraphQLClient {

    // TODO createPersistedQueryLink({useGETForHashedQueries: true}).concat( )

    constructor(options) {
        const url = (options && options.baseUrl) || 'http://localhost:3000';
        this.client = (options && options.client) || new ApolloClient({
            link: createHttpLink({
                uri: `${url}/graphql`,
                useGETForQueries: true,
                fetch,
            }),
            cache: new InMemoryCache(),
        });

    }

    // TODO             fetchPolicy: 'network-only',

    findConsumerWithOrders(consumerId) {
        return this.client.query({
            variables: { cid: consumerId},
            query: gql`
                query foo($cid : Int!) { 
                    consumer(consumerId: $cid)  { 
                        id 
                        firstName
                        lastName
                        orders { orderId restaurant { name } }
                     } 
                }  `,
        })
    }


}

module.exports = {FtgoGraphQLClient};
