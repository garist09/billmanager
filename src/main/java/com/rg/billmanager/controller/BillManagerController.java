package com.rg.billmanager.controller;

import com.rg.billmanager.dto.ServerConfig;
import com.rg.billmanager.service.BillManagerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class BillManagerController {
    private final BillManagerService billManagerService;

    @GetMapping(path = "/pricing-plans")
    public ResponseEntity<Map<String, List<ServerConfig>>> getPricingPlans(@RequestParam String baseUrl,
                                                                           @RequestParam String authData,
                                                                           @RequestParam Integer providerId,
                                                                           @RequestParam(required = false)
                                                                           Integer datacenterId) {
        return ResponseEntity.ok(billManagerService.getPricingPlans(baseUrl, authData, providerId, datacenterId));
    }
}
