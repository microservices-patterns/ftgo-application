package net.chrisrichardson.ftgo.deliveryservice.domain;

import net.chrisrichardson.ftgo.common.Address;
import net.chrisrichardson.ftgo.deliveryservice.api.web.ActionInfo;
import net.chrisrichardson.ftgo.deliveryservice.api.web.DeliveryInfo;
import net.chrisrichardson.ftgo.deliveryservice.api.web.DeliveryStatus;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class DeliveryService {

  private RestaurantRepository restaurantRepository;
  private DeliveryRepository deliveryRepository;
  private CourierRepository courierRepository;
  private Random random = new Random();

  public DeliveryService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository, CourierRepository courierRepository) {
    this.restaurantRepository = restaurantRepository;
    this.deliveryRepository = deliveryRepository;
    this.courierRepository = courierRepository;
  }

  public void createRestaurant(long restaurantId, String restaurantName, Address address) {
    restaurantRepository.save(Restaurant.create(restaurantId, restaurantName, address));
  }

  public void createDelivery(long orderId, long restaurantId, Address deliveryAddress) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId).get();
    deliveryRepository.save(Delivery.create(orderId, restaurantId, restaurant.getAddress(), deliveryAddress));
  }

  public void scheduleDelivery(long orderId, LocalDateTime readyBy) {
    Delivery delivery = deliveryRepository.findById(orderId).get();

    // Stupid implementation

    List<Courier> couriers = courierRepository.findAllAvailable();
    Courier courier = couriers.get(random.nextInt(couriers.size()));
    courier.addAction(Action.makePickup(delivery.getId(), delivery.getPickupAddress(), readyBy));
    courier.addAction(Action.makeDropoff(delivery.getId(), delivery.getDeliveryAddress(), readyBy.plusMinutes(30)));

    delivery.schedule(readyBy, courier.getId());

  }

  public void cancelDelivery(long orderId) {
    Delivery delivery = deliveryRepository.findById(orderId).get();
    Long assignedCourierId = delivery.getAssignedCourier();
    delivery.cancel();
    if (assignedCourierId != null) {
      Courier courier = courierRepository.findById(assignedCourierId).get();
      courier.cancelDelivery(delivery.getId());
    }

  }



  // notePickedUp
  // noteDelivered
  // noteLocation

  void noteAvailable(long courierId) {
    courierRepository.findOrCreateCourier(courierId).noteAvailable();
  }

  void noteUnavailable(long courierId) {
    courierRepository.findOrCreateCourier(courierId).noteUnavailable();
  }

  private Courier findOrCreateCourier(long courierId) {
    Courier courier = Courier.create(courierId);
    try {
      return courierRepository.save(courier);
    } catch (DuplicateKeyException e) {
      return courierRepository.findById(courierId).get();
    }
  }

  @Transactional
  public void updateAvailability(long courierId, boolean available) {
    if (available)
      noteAvailable(courierId);
    else
      noteUnavailable(courierId);
  }


  // getCourierRoute()

  @Transactional
  public Optional<DeliveryStatus> getDeliveryInfo(long deliveryId) {
    return deliveryRepository.findById(deliveryId).map(delivery -> {
      Long assignedCourier = delivery.getAssignedCourier();
      List<Action> courierActions = Collections.EMPTY_LIST;
      if (assignedCourier != null) {
        Courier courier = courierRepository.findById(assignedCourier).get();
        courierActions = courier.actionsForDelivery(deliveryId);
      }
      return makeDeliveryStatus(delivery, assignedCourier, courierActions);
    });
  }

  private DeliveryStatus makeDeliveryStatus(Delivery delivery, Long assignedCourier, List<Action> courierActions) {
    return new DeliveryStatus(makeDeliveryInfo(delivery), assignedCourier, courierActions.stream().map(action -> makeActionInfo(action)).collect(Collectors.toList()));
  }

  private DeliveryInfo makeDeliveryInfo(Delivery delivery) {
    return new DeliveryInfo(delivery.getId(), delivery.getState());
  }

  private ActionInfo makeActionInfo(Action action) {
    return new ActionInfo(action.getType());
  }
}
