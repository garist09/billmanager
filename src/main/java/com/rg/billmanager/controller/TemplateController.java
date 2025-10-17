package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.contracts.responses.TemplatePlansResponse;
import com.rg.billmanager.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.rg.billmanager.enums.FunctionName.DEDICATED_SERVERS_ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.DEDICATED_SERVER_EDIT;
import static com.rg.billmanager.enums.FunctionName.SERVER_AUCTION_EDIT;
import static com.rg.billmanager.enums.FunctionName.SERVER_AUCTION_ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.VIRTUAL_PRIVATE_SERVER_EDIT;
import static com.rg.billmanager.enums.FunctionName.VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM;

@RestController
@AllArgsConstructor
@Tag(name = "Template management", description = "Endpoints for managing OS templates and server configurations")
public class TemplateController {
    private final TemplateService templateService;

    @Operation(
            summary = "Retrieve VPS template configurations",
            description = "Fetches available OS templates for Virtual Private Server (VPS) plans.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved VPS templates",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TemplatePlansResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/vps/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getVirtualPrivateServersOsTemplates(@RequestParam String baseUrl,
                                                                                     @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId,
         VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM.getName()));
    }

    @Operation(
            summary = "Retrieve Dedicated Server template configurations",
            description = "Fetches available OS templates for Dedicated Server plans.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved Dedicated Server templates",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TemplatePlansResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/ds/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getDedicatedServersOsTemplates(@RequestParam String baseUrl,
                                                                                @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId,
         DEDICATED_SERVERS_ORDER_PARAM.getName()));
    }

    @Operation(
            summary = "Retrieve Server Auction template configurations",
            description = "Fetches available OS templates for Server Auction plans.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved Server Auction templates",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TemplatePlansResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/auction/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getServerAuctionOsTemplates(@RequestParam String baseUrl,
                                                                             @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId,
         SERVER_AUCTION_ORDER_PARAM.getName()));
    }

    @Operation(
            summary = "Update VPS server information",
            description = "Updates configuration details for a specific Virtual Private Server (VPS) order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "VPS information updated successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping(path = "/vps/server-info")
    public ResponseEntity<?> updateVirtualPrivateServerInfo(@RequestBody UpdateServerInfoRequest updateServerInfoRequest)
            throws JsonProcessingException {
        templateService.updateServerInfo(updateServerInfoRequest, VIRTUAL_PRIVATE_SERVER_EDIT.getName());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order updated successfully",
                "orderId", updateServerInfoRequest.getId()
        ));
    }

    @Operation(
            summary = "Update Dedicated Server information",
            description = "Updates configuration details for a specific Dedicated Server order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dedicated Server information updated successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping(path = "/ds/server-info")
    public ResponseEntity<?> updateDedicatedServerInfo(@RequestBody UpdateServerInfoRequest updateServerInfoRequest)
            throws JsonProcessingException {
        templateService.updateServerInfo(updateServerInfoRequest, DEDICATED_SERVER_EDIT.getName());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order updated successfully",
                "orderId", updateServerInfoRequest.getId()
        ));
    }

    @Operation(
            summary = "Update Server Auction information",
            description = "Updates configuration details for a specific Server Auction order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Server Auction information updated successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping(path = "/auction/server-info")
    public ResponseEntity<?> updateAuctionServerInfo(@RequestBody UpdateServerInfoRequest updateServerInfoRequest)
            throws JsonProcessingException {
        templateService.updateServerInfo(updateServerInfoRequest, SERVER_AUCTION_EDIT.getName());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order updated successfully",
                "orderId", updateServerInfoRequest.getId()
        ));
    }

    @Operation(
            summary = "Retrieve VPS server information",
            description = "Fetches detailed information for a list of Virtual Private Server (VPS) orders.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved VPS server information",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServerInfoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/vps/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getVirtualPrivateServerInfo(@RequestParam String baseUrl,
                                                                                @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, VIRTUAL_PRIVATE_SERVER_EDIT.getName()));
    }

    @Operation(
            summary = "Retrieve Dedicated Server information",
            description = "Fetches detailed information for a list of Dedicated Server orders.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved Dedicated Server information",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServerInfoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/ds/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getDedicatedServerInfo(@RequestParam String baseUrl,
                                                                           @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, DEDICATED_SERVER_EDIT.getName()));
    }

    @Operation(
            summary = "Retrieve Server Auction information",
            description = "Fetches detailed information for a list of Server Auction orders.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved Server Auction information",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServerInfoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping(path = "/auction/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getServerAuctionInfo(@RequestParam String baseUrl,
                                                                         @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, SERVER_AUCTION_EDIT.getName()));
    }
}
