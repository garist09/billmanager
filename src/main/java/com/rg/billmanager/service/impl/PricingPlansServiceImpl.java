package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.config.AuthProperties;
import com.rg.billmanager.contracts.requests.PricingPlanRequest;
import com.rg.billmanager.dto.ServerConfig;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.exception_handler.exception.InvalidRequestException;
import com.rg.billmanager.service.PricingPlansService;
import com.rg.billmanager.utility.ErrorUtility;
import com.rg.billmanager.utility.parser.BillManagerParser;
import com.rg.billmanager.utility.requestbuilder.UrlBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingPlansServiceImpl implements PricingPlansService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UrlBuilderRegistry urlBuilderRegistry;
    private final AuthProperties authProperties;
    private final ErrorUtility errorUtility;
    private final BillManagerParser billManagerParser;

    @Override
    public Map<String, List<ServerConfig>> getPricingPlans(String baseUrl, Integer datacenterId, String function)
            throws JsonProcessingException {
        String authData = authProperties.getAuthData(baseUrl);

        JsonNode json = getPricingPlansJson(baseUrl, authData, datacenterId, function);

        List<Map<String, String>> datacenters = new ArrayList<>();
        JsonNode valueNodes = json.path("doc").path("slist").get(0).path("val");
        fillDatacenters(datacenters, valueNodes);

        List<ServerConfig> serverConfigList = new ArrayList<>();

        if (datacenterId != null) {
            datacenters = datacenters.stream()
                    .filter(datacenter -> datacenter.get("id").equals(datacenterId.toString()))
                    .collect(Collectors.toList());
        }

        for (Map<String, String> datacenter : datacenters) {
            Integer id = Integer.valueOf(datacenter.get("id"));

            JsonNode jsonNode = getPricingPlansJson(baseUrl, authData, id, function);
            billManagerParser.parseServerConfigs(jsonNode, datacenter, serverConfigList, id);
        }

        return serverConfigList.stream().collect(Collectors.groupingBy(ServerConfig::getLocation));
    }

    private JsonNode getPricingPlansJson(String baseUrl, String authData, Integer datacenterId, String function)
            throws JsonProcessingException {
        RequestUrlBuilder<PricingPlanRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.PRICING_PLAN);

        String url = urlBuilder.buildUrl(new PricingPlanRequest(baseUrl, authData, datacenterId), function);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        String error = errorUtility.parseJsonErrorMessage(response.getBody());
        if (!error.isEmpty()) {
            throw new InvalidRequestException(error);
        }

        return objectMapper.readTree(response.getBody());
    }

    private void fillDatacenters(List<Map<String, String>> datacenters, JsonNode valueNodes) {
        Map<String, String> dc;
        for (JsonNode node : valueNodes) {
            dc = new HashMap<>();
            String key = node.path("$key").asText();
            String value = node.path("$").asText();

            dc.put("id", key);
            dc.put("name", value);
            datacenters.add(dc);
        }
    }
}
