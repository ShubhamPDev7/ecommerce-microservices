package com.ShubhamPDev7.ecommerce.shipping_service.repository;

import com.ShubhamPDev7.ecommerce.shipping_service.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
