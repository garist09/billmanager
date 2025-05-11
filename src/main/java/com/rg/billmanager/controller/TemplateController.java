package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ServerInfoRequest;
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

import java.util.Map;

@RestController
@AllArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping(path = "/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getOsTemplates(@RequestParam String baseUrl,
                                                                @RequestParam String authData,
                                                                @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, authData, externalId));
    }

    @PostMapping(path = "/server-info")
    public ResponseEntity<?> updateServerInfo(@RequestBody ServerInfoRequest serverInfoRequest)
            throws JsonProcessingException {
        templateService.updateServerInfo(serverInfoRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order created successfully",
                "orderId", serverInfoRequest.getId()
        ));
    }

    @GetMapping(path = "/server-info")
    public ResponseEntity<ServerInfoResponse> getServerInfo(@RequestParam String baseUrl,
                                                            @RequestParam String authData,
                                                            @RequestParam Integer orderId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, authData, orderId));
    }
}
