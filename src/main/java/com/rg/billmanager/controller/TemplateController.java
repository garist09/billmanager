package com.rg.billmanager.controller;

import com.rg.billmanager.dto.template.TemplatePlans;
import com.rg.billmanager.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping(path = "/template-configuration")
    public ResponseEntity<TemplatePlans> getOsTemplates(@RequestParam String baseUrl,
                                                        @RequestParam String authData,
                                                        @RequestParam Integer externalId) {
        return ResponseEntity.ok(templateService.getTemplatesForPlans(baseUrl, authData, externalId));
    }
}
