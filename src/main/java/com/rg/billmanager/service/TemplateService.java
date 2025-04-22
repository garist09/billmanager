package com.rg.billmanager.service;

import com.rg.billmanager.dto.template.OsTemplate;
import com.rg.billmanager.dto.template.prices.TemplatePeriodPrices;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    Map<String, List<OsTemplate>> getTemplatesForPlans(String baseUrl, String authData, Integer externalId);
    Map<String, TemplatePeriodPrices> getTemplatesPrices(String baseUrl, String authData, Integer externalId);
}
