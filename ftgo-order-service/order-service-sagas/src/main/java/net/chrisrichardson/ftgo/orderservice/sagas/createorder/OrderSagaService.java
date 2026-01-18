package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderNotFoundException;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRevision;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class OrderSagaService {

    private final OrderRepository orderRepository;
    private final SagaInstanceFactory sagaInstanceFactory;
    private final CreateOrderSaga createOrderSaga;
    private final CancelOrderSaga cancelOrderSaga;
    private final ReviseOrderSaga reviseOrderSaga;

    public OrderSagaService(OrderRepository orderRepository,
                            SagaInstanceFactory sagaInstanceFactory,
                            CreateOrderSaga createOrderSaga,
                            CancelOrderSaga cancelOrderSaga,
                            ReviseOrderSaga reviseOrderSaga) {
        this.orderRepository = orderRepository;
        this.sagaInstanceFactory = sagaInstanceFactory;
        this.createOrderSaga = createOrderSaga;
        this.cancelOrderSaga = cancelOrderSaga;
        this.reviseOrderSaga = reviseOrderSaga;
    }

    public Order createOrder(long consumerId, long restaurantId,
                             DeliveryInformation deliveryInformation,
                             List<MenuItemIdAndQuantity> lineItems) {
        CreateOrderSagaState data = new CreateOrderSagaState(consumerId, restaurantId, deliveryInformation, lineItems);
        sagaInstanceFactory.create(createOrderSaga, data);
        return orderRepository.findById(data.getOrderId()).get();
    }

    public Order cancel(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        CancelOrderSagaData sagaData = new CancelOrderSagaData(order.getConsumerId(), orderId, order.getOrderTotal());
        sagaInstanceFactory.create(cancelOrderSaga, sagaData);
        return order;
    }

    public Order reviseOrder(long orderId, OrderRevision orderRevision) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        ReviseOrderSagaData sagaData = new ReviseOrderSagaData(order.getConsumerId(), orderId, null, orderRevision);
        sagaInstanceFactory.create(reviseOrderSaga, sagaData);
        order.getOrderTotal(); // Initialize lazy-loaded lineItems collection
        return order;
    }
}
