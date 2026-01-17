package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import net.chrisrichardson.ftgo.orderservice.domain.DeliveryInformation;
import net.chrisrichardson.ftgo.orderservice.domain.MenuItemIdAndQuantity;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class OrderSagaService {

    private final OrderRepository orderRepository;
    private final SagaInstanceFactory sagaInstanceFactory;
    private final CreateOrderSaga createOrderSaga;

    public OrderSagaService(OrderRepository orderRepository,
                            SagaInstanceFactory sagaInstanceFactory,
                            CreateOrderSaga createOrderSaga) {
        this.orderRepository = orderRepository;
        this.sagaInstanceFactory = sagaInstanceFactory;
        this.createOrderSaga = createOrderSaga;
    }

    public Order createOrder(long consumerId, long restaurantId,
                             DeliveryInformation deliveryInformation,
                             List<MenuItemIdAndQuantity> lineItems) {
        CreateOrderSagaState data = new CreateOrderSagaState(consumerId, restaurantId, deliveryInformation, lineItems);
        sagaInstanceFactory.create(createOrderSaga, data);
        return orderRepository.findById(data.getOrderId()).get();
    }
}
