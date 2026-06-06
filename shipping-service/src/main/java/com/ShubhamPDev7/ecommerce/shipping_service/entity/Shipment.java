package com.ShubhamPDev7.ecommerce.shipping_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String shippingAddress;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
}
