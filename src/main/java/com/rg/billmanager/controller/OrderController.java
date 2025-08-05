package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(path = "/order")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest createOrderRequest) throws JsonProcessingException {
        String orderId = orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order created successfully",
                "orderId", orderId,
                "externalId", createOrderRequest.getExternalId(),
                "datacenterId", createOrderRequest.getDatacenterId(),
                "remoteId", createOrderRequest.getRemoteId()
        ));
    }

    @PostMapping(path = "/extend-order")
    public ResponseEntity<?> extendService(@RequestBody ExtendOrderRequest extendOrderRequest)
            throws JsonProcessingException {
        orderService.extendService(extendOrderRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order extended successfully",
                "orderId", extendOrderRequest.getOrderId()
        ));
    }

//    @GetMapping(path = "/cart-items")
//    public ResponseEntity<CartItemsResponse> getCartItems(@RequestParam String baseUrl, @RequestParam String authData)
//            throws JsonProcessingException {
//        return ResponseEntity.ok(orderService.getCartItems(baseUrl, authData));
//    }
//
//    @PostMapping(path = "/remove-cart-item")
//    public ResponseEntity<?> removeCartItem(@RequestBody DeleteOrderRequest deleteOrderRequest) throws JsonProcessingException {
//        orderService.removeCartItem(deleteOrderRequest);
//        return ResponseEntity.ok(Map.of(
//                "status", "success",
//                "message", "Item successfully have been removed"
//        ));
//    }
}
