package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.contracts.requests.DeleteOrderRequest;
import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;

import java.util.List;

public interface OrderService {
    String createOrder(CreateOrderRequest createOrderRequest) throws JsonProcessingException;
    void extendService(ExtendOrderRequest extendOrderRequest) throws JsonProcessingException;
    CartItemsResponse getCartItems(String baseUrl, String authData) throws JsonProcessingException;
    void removeCartItem(DeleteOrderRequest deleteOrderRequest) throws JsonProcessingException;
}
