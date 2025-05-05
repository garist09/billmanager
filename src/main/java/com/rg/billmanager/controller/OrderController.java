package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.order.DeleteOrderRequest;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;
import com.rg.billmanager.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(path = "/add-cart-item")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) throws JsonProcessingException {
        orderService.addItemToCart(orderRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order created successfully"
        ));
    }

    @GetMapping(path = "/cart-items")
    public ResponseEntity<CartItemsResponse> getCartItems(@RequestParam String baseUrl, @RequestParam String authData)
            throws JsonProcessingException {
        return ResponseEntity.ok(orderService.getCartItems(baseUrl, authData));
    }

    @PostMapping(path = "/remove-cart-item")
    public ResponseEntity<?> removeCartItem(@RequestBody DeleteOrderRequest deleteOrderRequest) throws JsonProcessingException {
        orderService.removeCartItem(deleteOrderRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Item successfully have been removed"
        ));
    }
}
