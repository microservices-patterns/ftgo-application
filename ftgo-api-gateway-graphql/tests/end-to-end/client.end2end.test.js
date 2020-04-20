const {app} = require("../../src/server.ts");
const {FtgoGraphQLClient} = require("../common/ftgo-graphql-client.js");


/*
var server;

beforeAll(() => {

    const PORT = 3000;
    server = app.listen(PORT);
    console.log("Server listing on port: ", PORT);

});

afterAll(() => {
    server.close();
});
*/

test('findConsumerWithOrders', () => {
    const client = new FtgoGraphQLClient({baseUrl: `http://${process.env.DOCKER_HOST_IP || "localhost"}:8088`});

    return client.findConsumerWithOrders("1")
        .then(result => {
            expect(result.errors).toBe(undefined);
            expect(result.data.consumer.id).toBe('1');
            expect(result.data.consumer.orders[0].restaurant.name).toBe('My Restaurant');
            console.log("result.data", JSON.stringify(result.data));
        });

});
