package com.ShubhamPDev7.ecommerce.order_service.service;

import com.ShubhamPDev7.ecommerce.order_service.clients.InventoryOpenFeignClient;
import com.ShubhamPDev7.ecommerce.order_service.dto.OrderRequestDto;
import com.ShubhamPDev7.ecommerce.order_service.entity.OrderItem;
import com.ShubhamPDev7.ecommerce.order_service.entity.OrderStatus;
import com.ShubhamPDev7.ecommerce.order_service.entity.Orders;
import com.ShubhamPDev7.ecommerce.order_service.repository.OrdersRepository;
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

    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
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
}
