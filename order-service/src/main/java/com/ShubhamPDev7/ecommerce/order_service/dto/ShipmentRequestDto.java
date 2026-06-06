package com.ShubhamPDev7.ecommerce.order_service.dto;

import lombok.Data;

@Data
public class ShipmentRequestDto {
    private Long id;
    private Long orderId;
    private String shippingAddress;
    private String status;
}
