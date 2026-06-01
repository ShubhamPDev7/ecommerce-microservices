package com.ShubhamPDev7.ecommerce.order_service.dto;

import lombok.Data;

@Data
public class OrderRequestItemDto {
    private Long id;
    private Long ProductId;
    private Integer quantity;
}
