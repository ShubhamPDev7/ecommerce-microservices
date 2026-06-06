package com.ShubhamPDev7.ecommerce.order_service.service;

import com.ShubhamPDev7.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.ShubhamPDev7.ecommerce.order_service.dto.OrderRequestDto;
import com.ShubhamPDev7.ecommerce.order_service.entity.OrderItem;
import com.ShubhamPDev7.ecommerce.order_service.entity.OrderStatus;
import com.ShubhamPDev7.ecommerce.order_service.entity.Orders;
import com.ShubhamPDev7.ecommerce.order_service.repository.OrdersRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;

    public List<OrderRequestDto> getAllOrders() {
        log.info("Fetching all orders");
        List<Orders> orders = ordersRepository.findAll();
        return orders.stream().map(order -> modelMapper.map(order, OrderRequestDto.class))
                .toList();
    }

    public OrderRequestDto getOrderById(Long id) {
        log.info("Fetching order with id {}", id);
        Orders order = ordersRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderRequestDto.class);
    }

//    @Retry(name = "inventoryRetry", fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod = "createOrderFallback")
//    @RateLimiter(name = "inventoryRateLimiter", fallbackMethod = "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Calling the create order method");
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);
        Orders orders =  modelMapper.map(orderRequestDto, Orders.class);
        for (OrderItem orderItem: orders.getItems()) {
            orderItem.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);
        Orders savedOrders = ordersRepository.save(orders);
        return modelMapper.map(savedOrders, OrderRequestDto.class);

    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Fallback occured due to : {}", throwable.getMessage());

        return new OrderRequestDto();
    }

    public OrderRequestDto cancelOrder(Long orderId) {
        log.info("Cancelling order with id {}", orderId);

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new RuntimeException("Order is already cancelled. Order id: " + orderId);
        }

        inventoryOpenFeignClient.restockItems(new OrderRequestDto());
        log.info("Items restocked successfully for order id {}", orderId);

        order.setOrderStatus(OrderStatus.CANCELLED);
        Orders cancelledOrder = ordersRepository.save(order);

        return modelMapper.map(cancelledOrder, OrderRequestDto.class);
    }
}
