package com.ShubhamPDev7.ecommerce.shipping_service.controller;

import com.ShubhamPDev7.ecommerce.shipping_service.dto.ShipmentRequestDto;
import com.ShubhamPDev7.ecommerce.shipping_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shipments")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/initiate")
    public ResponseEntity<ShipmentRequestDto> initiateShipping(@RequestBody ShipmentRequestDto shipmentRequestDto) {
        log.info("Shipment initiation request for order id: {}", shipmentRequestDto.getOrderId());
        ShipmentRequestDto response = shippingService.initiateShipping(shipmentRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentRequestDto> getShipmentStatus(@PathVariable Long id){
        log.info("Status check for shipment id: {}", id);
        ShipmentRequestDto response = shippingService.getShipmentStatus(id);
        return ResponseEntity.ok(response);
    }
}
