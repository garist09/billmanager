package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ServerInfoRequest;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.contracts.responses.TemplatePlansResponse;

public interface TemplateService {
    TemplatePlansResponse getTemplatesForPlans(String baseUrl, String authData, Integer externalId) throws JsonProcessingException;
    void updateServerInfo(ServerInfoRequest serverInfoRequest) throws JsonProcessingException;
    ServerInfoResponse getServerInfo(String baseUrl, String authData, Integer orderId) throws JsonProcessingException;
}
