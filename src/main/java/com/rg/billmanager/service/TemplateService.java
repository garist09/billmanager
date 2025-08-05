package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.contracts.responses.TemplatePlansResponse;

import java.util.List;

public interface TemplateService {
    TemplatePlansResponse getTemplatesForPlans(String baseUrl, String authData, Integer externalId) throws JsonProcessingException;
    void updateServerInfo(UpdateServerInfoRequest updateServerInfoRequest) throws JsonProcessingException;
    List<ServerInfoResponse> getServerInfo(String baseUrl, String authData, List<Integer> orderIds) throws JsonProcessingException;
}
