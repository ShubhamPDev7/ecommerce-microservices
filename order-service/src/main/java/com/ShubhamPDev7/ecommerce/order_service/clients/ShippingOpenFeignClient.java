package com.ShubhamPDev7.ecommerce.order_service.clients;

import com.ShubhamPDev7.ecommerce.order_service.dto.ShipmentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipping-service")
public interface ShippingOpenFeignClient {

    @PostMapping("/shipments/initiate")
    ShipmentRequestDto initiateShipping(@RequestBody ShipmentRequestDto shipmentRequestDto);
}
