package com.ShubhamPDev7.ecommerce.shipping_service.dto;

import com.ShubhamPDev7.ecommerce.shipping_service.entity.ShipmentStatus;
import lombok.Data;

@Data
public class ShipmentRequestDto {
    private Long id;
    private Long orderId;
    private String shippingAddress;
    private ShipmentStatus status;
}
