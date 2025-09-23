package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.contracts.responses.TemplatePlansResponse;
import com.rg.billmanager.service.TemplateService;
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
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping(path = "/vps/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getVirtualPrivateServersOsTemplates(@RequestParam String baseUrl,
                                                                                     @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId,
         VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM.getName()));
    }

    @GetMapping(path = "/ds/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getDedicatedServersOsTemplates(@RequestParam String baseUrl,
                                                                                @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId,
         DEDICATED_SERVERS_ORDER_PARAM.getName()));
    }

    @GetMapping(path = "/auction/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getServerAuctionOsTemplates(@RequestParam String baseUrl,
                                                                             @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId,
         SERVER_AUCTION_ORDER_PARAM.getName()));
    }

    @PostMapping(path = "/server-info")
    public ResponseEntity<?> updateServerInfo(@RequestBody UpdateServerInfoRequest updateServerInfoRequest)
            throws JsonProcessingException {
        templateService.updateServerInfo(updateServerInfoRequest, VIRTUAL_PRIVATE_SERVER_EDIT.getName());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order updated successfully",
                "orderId", updateServerInfoRequest.getId()
        ));
    }

    @GetMapping(path = "/vps/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getVirtualPrivateServerInfo(@RequestParam String baseUrl,
                                                                                @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, VIRTUAL_PRIVATE_SERVER_EDIT.getName()));
    }

    @GetMapping(path = "/ds/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getDedicatedServerInfo(@RequestParam String baseUrl,
                                                                           @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, DEDICATED_SERVER_EDIT.getName()));
    }

    @GetMapping(path = "/auction/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getServerAuctionInfo(@RequestParam String baseUrl,
                                                                         @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, SERVER_AUCTION_EDIT.getName()));
    }
}
