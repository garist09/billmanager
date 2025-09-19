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

import static com.rg.billmanager.enums.FunctionName.ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.VDS_EDIT;

@RestController
@AllArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping(path = "/template-configuration")
    public ResponseEntity<TemplatePlansResponse> getOsTemplates(@RequestParam String baseUrl,
                                                                @RequestParam Integer externalId)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, externalId, ORDER_PARAM.getName()));
    }

    @PostMapping(path = "/server-info")
    public ResponseEntity<?> updateServerInfo(@RequestBody UpdateServerInfoRequest updateServerInfoRequest)
            throws JsonProcessingException {
        templateService.updateServerInfo(updateServerInfoRequest);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Order updated successfully",
                "orderId", updateServerInfoRequest.getId()
        ));
    }

    @GetMapping(path = "/server-info")
    public ResponseEntity<List<ServerInfoResponse>> getServerInfo(@RequestParam String baseUrl,
                                                                  @RequestParam List<Integer> orderIds)
            throws JsonProcessingException {
        return ResponseEntity.ok(templateService.getServerInfo(baseUrl, orderIds, VDS_EDIT.getName()));
    }
}
