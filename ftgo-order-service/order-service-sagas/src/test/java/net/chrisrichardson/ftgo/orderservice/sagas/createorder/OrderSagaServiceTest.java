package net.chrisrichardson.ftgo.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.domain.Order;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRepository;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import net.chrisrichardson.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import net.chrisrichardson.ftgo.orderservice.domain.OrderRevision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class OrderSagaServiceTest {

    private OrderRepository orderRepository;
    private SagaInstanceFactory sagaInstanceFactory;
    private CreateOrderSaga createOrderSaga;
    private CancelOrderSaga cancelOrderSaga;
    private ReviseOrderSaga reviseOrderSaga;
    private OrderSagaService orderSagaService;

    private static final long ORDER_ID = 99L;
    private static final long CONSUMER_ID = 1L;
    private static final Money ORDER_TOTAL = new Money("100.00");

    @BeforeEach
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        sagaInstanceFactory = mock(SagaInstanceFactory.class);
        createOrderSaga = mock(CreateOrderSaga.class);
        cancelOrderSaga = mock(CancelOrderSaga.class);
        reviseOrderSaga = mock(ReviseOrderSaga.class);
        orderSagaService = new OrderSagaService(orderRepository, sagaInstanceFactory, createOrderSaga, cancelOrderSaga, reviseOrderSaga);
    }

    @Test
    public void shouldCancelOrder() {
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(ORDER_ID);
        when(order.getConsumerId()).thenReturn(CONSUMER_ID);
        when(order.getOrderTotal()).thenReturn(ORDER_TOTAL);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        Order result = orderSagaService.cancel(ORDER_ID);

        assertEquals(order, result);

        ArgumentCaptor<CancelOrderSagaData> sagaDataCaptor = ArgumentCaptor.forClass(CancelOrderSagaData.class);
        verify(sagaInstanceFactory).create(same(cancelOrderSaga), sagaDataCaptor.capture());

        CancelOrderSagaData sagaData = sagaDataCaptor.getValue();
        assertEquals(ORDER_ID, sagaData.getOrderId());
        assertEquals(CONSUMER_ID, sagaData.getConsumerId());
        assertEquals(ORDER_TOTAL, sagaData.getOrderTotal());
    }

    @Test
    public void shouldReviseOrder() {
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(ORDER_ID);
        when(order.getConsumerId()).thenReturn(CONSUMER_ID);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        OrderRevision orderRevision = mock(OrderRevision.class);

        Order result = orderSagaService.reviseOrder(ORDER_ID, orderRevision);

        assertEquals(order, result);

        ArgumentCaptor<ReviseOrderSagaData> sagaDataCaptor = ArgumentCaptor.forClass(ReviseOrderSagaData.class);
        verify(sagaInstanceFactory).create(same(reviseOrderSaga), sagaDataCaptor.capture());

        ReviseOrderSagaData sagaData = sagaDataCaptor.getValue();
        assertEquals(ORDER_ID, sagaData.getOrderId());
        assertEquals(CONSUMER_ID, sagaData.getConsumerId());
        assertEquals(orderRevision, sagaData.getOrderRevision());
    }
}
