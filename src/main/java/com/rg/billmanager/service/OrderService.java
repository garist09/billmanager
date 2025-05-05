package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;
import org.springframework.web.bind.annotation.RequestParam;

public interface OrderService {
    void createOrder(OrderRequest orderRequest) throws JsonProcessingException;
    CartItemsResponse getCartItems(String baseUrl, String authData) throws JsonProcessingException;
}
