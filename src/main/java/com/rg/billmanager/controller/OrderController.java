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

import static com.rg.billmanager.enums.FunctionName.DEDICATED_SERVERS_ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.SERVER_AUCTION_ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.SERVICE_PROLONG;
import static com.rg.billmanager.enums.FunctionName.VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM;

@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(path = "/vps/order")
    public ResponseEntity<?> createVirtualPrivateServerOrder(@RequestBody CreateOrderRequest createOrderRequest)
            throws JsonProcessingException {
        String orderId = orderService.createOrder(createOrderRequest, VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM.getName());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order created successfully",
                "orderId", orderId,
                "externalId", createOrderRequest.getExternalId(),
                "datacenterId", createOrderRequest.getDatacenterId(),
                "remoteId", createOrderRequest.getRemoteId()
        ));
    }

    @PostMapping(path = "/ds/order")
    public ResponseEntity<?> createDedicatedServerOrder(@RequestBody CreateOrderRequest createOrderRequest)
            throws JsonProcessingException {
        String orderId = orderService.createOrder(createOrderRequest, DEDICATED_SERVERS_ORDER_PARAM.getName());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order created successfully",
                "orderId", orderId,
                "externalId", createOrderRequest.getExternalId(),
                "datacenterId", createOrderRequest.getDatacenterId(),
                "remoteId", createOrderRequest.getRemoteId()
        ));
    }

    @PostMapping(path = "/auction/order")
    public ResponseEntity<?> createServerAuctionOrder(@RequestBody CreateOrderRequest createOrderRequest)
            throws JsonProcessingException {
        String orderId = orderService.createOrder(createOrderRequest, SERVER_AUCTION_ORDER_PARAM.getName());
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
        orderService.extendService(extendOrderRequest, SERVICE_PROLONG.getName());
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
