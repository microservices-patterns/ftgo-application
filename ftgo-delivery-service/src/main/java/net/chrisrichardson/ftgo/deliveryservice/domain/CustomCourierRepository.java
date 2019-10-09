package net.chrisrichardson.ftgo.deliveryservice.domain;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomCourierRepository {

  Courier findOrCreateCourier(long courierId);

}
