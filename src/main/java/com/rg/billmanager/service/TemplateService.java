package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.contracts.responses.TemplatePlansResponse;

import java.util.List;

public interface TemplateService {
    TemplatePlansResponse getTemplatesForPlans(String baseUrl, Integer externalId, String function)
            throws JsonProcessingException;
    void updateServerInfo(UpdateServerInfoRequest updateServerInfoRequest) throws JsonProcessingException;
    List<ServerInfoResponse> getServerInfo(String baseUrl, List<Integer> orderIds, String function) throws JsonProcessingException;
}
