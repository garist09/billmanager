package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.ServerConfig;
import com.rg.billmanager.service.BillManagerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.rg.billmanager.enums.FunctionName.DEDICATED_SERVERS_PRICELIST;
import static com.rg.billmanager.enums.FunctionName.VIRTUAL_PRIVATE_SERVERS_PRICELIST;

@RestController
@AllArgsConstructor
public class BillManagerController {
    private final BillManagerService billManagerService;

    @GetMapping(path = "/vps/pricing-plans")
    public ResponseEntity<Map<String, List<ServerConfig>>> getVirtualPrivateServersPricingPlans(@RequestParam String baseUrl,
                                                                                                @RequestParam(required = false)
                                                                                                Integer datacenterId)
            throws JsonProcessingException {
        return ResponseEntity.ok(billManagerService.getPricingPlans(baseUrl, datacenterId,
        VIRTUAL_PRIVATE_SERVERS_PRICELIST.getName()));
    }

    @GetMapping(path = "/ds/pricing-plans")
    public ResponseEntity<Map<String, List<ServerConfig>>> getDedicatedServerPricingPlans(@RequestParam String baseUrl,
                                                                                          @RequestParam(required = false)
                                                                                          Integer datacenterId)
            throws JsonProcessingException {
        return ResponseEntity.ok(billManagerService.getPricingPlans(baseUrl, datacenterId,
        DEDICATED_SERVERS_PRICELIST.getName()));
    }
}
