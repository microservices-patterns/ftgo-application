package contracts.http;

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url '/orders/99'
    }
    response {
        status 200
        headers {
            header('Content-Type': 'application/json;charset=UTF-8')
        }
        body('''{"orderId" : "99", "state" : "APPROVAL_PENDING"}''')
    }
}