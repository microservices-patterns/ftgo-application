package net.chrisrichardson.ftgo.deliveryservice.domain;

public interface CustomCourierRepository {

  Courier findOrCreateCourier(long courierId);

}
