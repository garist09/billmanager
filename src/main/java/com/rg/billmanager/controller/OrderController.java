package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.order.OrderRequest;
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
    public ResponseEntity<?> getPricingPlans(@RequestBody OrderRequest orderRequest) throws JsonProcessingException {
        orderService.createOrder(orderRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order created successfully"
        ));
    }
}
