package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.order.DeleteOrderRequest;
import com.rg.billmanager.dto.order.ExtendOrderRequest;
import com.rg.billmanager.dto.order.OrderRequest;
import com.rg.billmanager.dto.order.OrderStatusResponse;
import com.rg.billmanager.dto.order.cart.items.CartItemsResponse;

import java.util.List;

public interface OrderService {
    String createOrder(OrderRequest orderRequest) throws JsonProcessingException;
    List<OrderStatusResponse> getOrderStatus(String baseUrl, String authData, List<String> remoteIds) throws JsonProcessingException;
    void extendService(ExtendOrderRequest extendOrderRequest) throws JsonProcessingException;
    CartItemsResponse getCartItems(String baseUrl, String authData) throws JsonProcessingException;
    void removeCartItem(DeleteOrderRequest deleteOrderRequest) throws JsonProcessingException;
}
