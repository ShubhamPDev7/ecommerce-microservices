package com.ShubhamPDev7.ecommerce.inventory_service.controller;

import com.ShubhamPDev7.ecommerce.inventory_service.clients.OrdersFeignClient;
import com.ShubhamPDev7.ecommerce.inventory_service.dto.OrderRequestDto;
import com.ShubhamPDev7.ecommerce.inventory_service.dto.ProductDto;
import com.ShubhamPDev7.ecommerce.inventory_service.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    private final OrdersFeignClient ordersFeignClient;

    @GetMapping("/fetchOrders")
    public String fetchFromOrderService(HttpServletRequest httpServletRequest) {

        log.info(httpServletRequest.getHeader("x-custom-header"));
//        ServiceInstance orderService = discoveryClient.getInstances("order-service").getFirst();

//        String response = restClient.get()
//                .uri(orderService.getUri()+"/core/helloOrders")
//                .retrieve()
//                .body(String.class);

        return ordersFeignClient.helloOrders();

    }



    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllInventory() {
        List<ProductDto> inventories = productService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto inventory = productService.getProductById(id);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto orderRequestDto) {
        Double totalPrice = productService.reduceStocks(orderRequestDto);
        return ResponseEntity.ok(totalPrice);
    }

    @PutMapping("/restock-items")
    public ResponseEntity<String> restockItem(@RequestBody OrderRequestDto orderRequestDto) {
        log.info("Restock request received for {} items", orderRequestDto.getItems().size());
        productService.restockItem(orderRequestDto);
        return ResponseEntity.ok("Item restocked successfully");
    }

}
