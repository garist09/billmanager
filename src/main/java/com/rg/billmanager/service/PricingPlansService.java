package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.ServerConfig;

import java.util.List;
import java.util.Map;

public interface PricingPlansService {
    Map<String, List<ServerConfig>> getPricingPlans(String baseUrl, Integer datacenterId, String function)
            throws JsonProcessingException;
}
