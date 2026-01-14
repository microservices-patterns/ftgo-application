package net.chrisrichardson.ftgo.deliveryservice.domain;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

public class CustomCourierRepositoryImpl implements CustomCourierRepository {

  @Autowired
  private EntityManager entityManager;

  @Override
  public Courier findOrCreateCourier(long courierId) {
    Courier courier = entityManager.find(Courier.class, courierId);
    if (courier == null) {
      courier = Courier.create(courierId);
      entityManager.persist(courier);
    }
    return courier;
  }
}
