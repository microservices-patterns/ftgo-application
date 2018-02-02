package contracts.http;

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url '/orders/555'
    }
    response {
        status 404
    }
}