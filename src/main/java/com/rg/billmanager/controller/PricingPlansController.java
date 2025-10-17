package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.ServerConfig;
import com.rg.billmanager.service.PricingPlansService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.rg.billmanager.enums.FunctionName.DEDICATED_SERVERS_PRICELIST;
import static com.rg.billmanager.enums.FunctionName.SERVER_AUCTION_PRICELIST;
import static com.rg.billmanager.enums.FunctionName.VIRTUAL_PRIVATE_SERVERS_PRICELIST;

@RestController
@AllArgsConstructor
@Tag(name = "Pricing plan management", description = "Endpoints for managing pricing plans")
public class PricingPlansController {
    private final PricingPlansService pricingPlansService;

    @Operation(summary = "Retrieve VPS pricing plans",
            description = "Fetches pricing plans for virtual private servers (VPS) based on the provided base URL and optional datacenter ID.",
            responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved VPS pricing plans",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServerConfig.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content)
    })
    @GetMapping(path = "/vps/pricing-plans")
    public ResponseEntity<Map<String, List<ServerConfig>>> getVirtualPrivateServersPricingPlans(@RequestParam String baseUrl,
                                                                                                @RequestParam(required = false)
                                                                                                Integer datacenterId)
            throws JsonProcessingException {
        return ResponseEntity.ok(pricingPlansService.getPricingPlans(baseUrl, datacenterId,
        VIRTUAL_PRIVATE_SERVERS_PRICELIST.getName()));
    }

    @Operation(
            summary = "Retrieve Dedicated Server pricing plans",
            description = "Fetches pricing plans for dedicated servers using the provided base URL and optional datacenter ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved Dedicated Server pricing plans",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ServerConfig.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/ds/pricing-plans")
    public ResponseEntity<Map<String, List<ServerConfig>>> getDedicatedServerPricingPlans(@RequestParam String baseUrl,
                                                                                          @RequestParam(required = false)
                                                                                          Integer datacenterId)
            throws JsonProcessingException {
        return ResponseEntity.ok(pricingPlansService.getPricingPlans(baseUrl, datacenterId,
        DEDICATED_SERVERS_PRICELIST.getName()));
    }

    @Operation(
            summary = "Retrieve Server Auction pricing plans",
            description = "Fetches pricing plans for server auctions based on the base URL and optional datacenter ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved Server Auction pricing plans",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ServerConfig.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/auction/pricing-plans")
    public ResponseEntity<Map<String, List<ServerConfig>>> getServerAuctionPricingPlans(@RequestParam String baseUrl,
                                                                                        @RequestParam(required = false)
                                                                                        Integer datacenterId)
            throws JsonProcessingException {
        return ResponseEntity.ok(pricingPlansService.getPricingPlans(baseUrl, datacenterId,
                SERVER_AUCTION_PRICELIST.getName()));
    }
}
