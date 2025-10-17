package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order management", description = "Endpoints for creating and managing server orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Create a Virtual Private Server (VPS) order",
            description = "Creates a new VPS order based on the provided request details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
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

    @Operation(
            summary = "Create a Dedicated Server order",
            description = "Creates a new dedicated server order based on the provided request details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
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

    @Operation(
            summary = "Create a Server Auction order",
            description = "Creates a new server auction order based on the provided request details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
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

    @Operation(
            summary = "Extend an existing service order",
            description = "Extends the duration of an existing service based on the provided order details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order extended successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid extension request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
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
}
