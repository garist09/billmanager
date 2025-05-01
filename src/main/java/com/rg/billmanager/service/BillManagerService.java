package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.dto.ServerConfig;

import java.util.List;
import java.util.Map;

public interface BillManagerService {
    Map<String, List<ServerConfig>> getPricingPlans(String baseUrl, String authData, Integer datacenterId) throws JsonProcessingException;
}
