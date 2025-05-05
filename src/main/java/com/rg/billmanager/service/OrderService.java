package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.order.DeleteOrderRequest;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;

public interface OrderService {
    void addItemToCart(OrderRequest orderRequest) throws JsonProcessingException;
    CartItemsResponse getCartItems(String baseUrl, String authData) throws JsonProcessingException;
    void removeCartItem(DeleteOrderRequest deleteOrderRequest) throws JsonProcessingException;
}
