package net.chrisrichardson.ftgo.orderservice.domain;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for order operations.
 * Implementation is provided by OrderServiceImpl in the main module.
 */
public interface OrderService {

    // REST API methods
    Order createOrder(long consumerId, long restaurantId, DeliveryInformation deliveryInformation,
                      List<MenuItemIdAndQuantity> lineItems);

    Order cancel(Long orderId);

    Order reviseOrder(long orderId, OrderRevision orderRevision);

    // Saga command handler methods
    void approveOrder(long orderId);

    void rejectOrder(long orderId);

    void beginCancel(long orderId);

    void undoCancel(long orderId);

    void confirmCancelled(long orderId);

    Optional<RevisedOrder> beginReviseOrder(long orderId, OrderRevision revision);

    void undoPendingRevision(long orderId);

    void confirmRevision(long orderId, OrderRevision revision);

    // Event handler methods
    void createMenu(long id, String name, List<MenuItem> menuItems);

    void reviseMenu(long id, List<MenuItem> menuItems);
}
