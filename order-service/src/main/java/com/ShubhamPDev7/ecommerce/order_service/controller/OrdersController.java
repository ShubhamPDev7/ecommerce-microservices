package com.ShubhamPDev7.ecommerce.order_service.controller;

import com.ShubhamPDev7.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.ShubhamPDev7.ecommerce.order_service.dto.OrderRequestDto;
import com.ShubhamPDev7.ecommerce.order_service.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
@Slf4j
public class OrdersController {

    private final OrdersService ordersService;


    @GetMapping("/helloOrders")
    public ResponseEntity<String> helloOrders() {
        return ResponseEntity.ok("Hello from Order Service");
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderRequestDto orderRequestDto1 = ordersService.createOrder(orderRequestDto);
        return ResponseEntity.ok(orderRequestDto1);

    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders() {
        log.info("Fetching all orders via controller");
        List<OrderRequestDto> orders = ordersService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto>getOrderById(@PathVariable Long id) {
        log.info("Fetching order with ID: {} via controller", id);
        OrderRequestDto order = ordersService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderRequestDto> cancelOrder(@PathVariable Long id) {
        log.info("Cancel order request received with id: {}",id);
        OrderRequestDto cancelledOrder = ordersService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }

}
