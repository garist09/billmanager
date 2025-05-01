package com.rg.billmanager.service;

import com.rg.billmanager.dto.template.TemplatePlans;

public interface TemplateService {
    TemplatePlans getTemplatesForPlans(String baseUrl, String authData, Integer externalId);
}
