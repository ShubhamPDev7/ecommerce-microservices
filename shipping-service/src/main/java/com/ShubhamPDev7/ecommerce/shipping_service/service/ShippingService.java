package com.ShubhamPDev7.ecommerce.shipping_service.service;

import com.ShubhamPDev7.ecommerce.shipping_service.dto.ShipmentRequestDto;
import com.ShubhamPDev7.ecommerce.shipping_service.entity.Shipment;
import com.ShubhamPDev7.ecommerce.shipping_service.entity.ShipmentStatus;
import com.ShubhamPDev7.ecommerce.shipping_service.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentRequestDto initiateShipping(ShipmentRequestDto dto) {
        log.info("Initiating shipping for order id: {}", dto.getOrderId());
        Shipment shipment = new Shipment();
        shipment.setOrderId(dto.getOrderId());
        shipment.setShippingAddress(dto.getShippingAddress());

        shipment.setStatus(ShipmentStatus.INITIATED);
        Shipment saved = shipmentRepository.save(shipment);
        log.info("Shipment created with id: {} for order: {}", saved.getId(), saved.getOrderId());

        ShipmentRequestDto responseDto = new ShipmentRequestDto();
        responseDto.setId(saved.getId());
        responseDto.setOrderId(saved.getOrderId());
        responseDto.setShippingAddress(saved.getShippingAddress());
        responseDto.setStatus(saved.getStatus());

        return responseDto;
    }
    public ShipmentRequestDto getShipmentStatus(Long shipmentId) {
        log.info("Fetching shipment status for id: {}", shipmentId);

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found for id: " + shipmentId));

        ShipmentRequestDto responseDto = new ShipmentRequestDto();
        responseDto.setId(shipment.getId());
        responseDto.setOrderId(shipment.getOrderId());
        responseDto.setShippingAddress(shipment.getShippingAddress());
        responseDto.setStatus(shipment.getStatus());
        return responseDto;
    }
}
