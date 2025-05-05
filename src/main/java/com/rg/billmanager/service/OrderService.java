package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.order.OrderRequest;

public interface OrderService {
    void createOrder(OrderRequest orderRequest) throws JsonProcessingException;
}
