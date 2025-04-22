package com.rg.billmanager.controller;

import com.rg.billmanager.dto.template.OsTemplate;
import com.rg.billmanager.dto.template.prices.TemplatePeriodPrices;
import com.rg.billmanager.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping(path = "/os-templates")
    public ResponseEntity<Map<String, List<OsTemplate>>> getOsTemplates(@RequestParam String baseUrl,
                                                                        @RequestParam String authData,
                                                                        @RequestParam Integer externalId) {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, authData, externalId));
    }

    @GetMapping(path = "/template-prices")
    public ResponseEntity<Map<String, TemplatePeriodPrices>> getTemplatePrices(@RequestParam String baseUrl,
                                                                               @RequestParam String authData,
                                                                               @RequestParam Integer externalId) {
        return ResponseEntity.ok(templateService.getTemplatesPrices(baseUrl, authData, externalId));
    }
}
