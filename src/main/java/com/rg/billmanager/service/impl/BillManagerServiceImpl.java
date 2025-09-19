package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.rg.billmanager.config.AuthProperties;
import com.rg.billmanager.contracts.requests.PricingPlanRequest;
import com.rg.billmanager.dto.ServerConfig;
import com.rg.billmanager.dto.ServerResources;
import com.rg.billmanager.enums.BillingPeriod;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.exception_handler.exception.InvalidRequestException;
import com.rg.billmanager.service.BillManagerService;
import com.rg.billmanager.utility.ErrorUtility;
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

import static com.rg.billmanager.constants.JsonFields.*;

@Service
@RequiredArgsConstructor
public class BillManagerServiceImpl implements BillManagerService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UrlBuilderRegistry urlBuilderRegistry;
    private final AuthProperties authProperties;
    private final ErrorUtility errorUtility;

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
            addServerConfig(baseUrl, authData, datacenter, serverConfigList, function);
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

    String getDetailValue(JsonNode detailArray, String keyName) {
        if (detailArray.isArray()) {
            for (JsonNode item : detailArray) {
                String name = item.path("name").path("$").asText("");
                if (name.equalsIgnoreCase(keyName)) {
                    return item.path("value").path("$").asText("");
                }
            }
        }

        return "";
    }

    String getJsonNodeValue(JsonNode server, String keyName) {
        String value = "";
        if (server.has(keyName)) {
            JsonNode node = server.get(keyName);
            if (node.has("$")) {
                value = node.get("$").asText();
            }
        }

        return value;
    }

    private ServerConfig parseServerConfigs(Integer externalId, JsonNode server,
                                            String datacenterName) {
        ServerResources serverResources = createServerResources(server);
        String name = getJsonNodeValue(server, TITLE);
        String description = getJsonNodeValue(server, DESCRIPTION);

        Map<String, String> billingCycleMap = new HashMap<>();
        String currency = "€";

        if (server.get(PRICES) != null && server.get(PRICES).get(PRICE) != null &&
                server.get(PRICES).get(PRICE).isArray()) {
            JsonNode pricesNode = server.get(PRICES).get(PRICE);

            currency = pricesNode.get(0).get(CURRENCY).get("$").asText("€");
            for (JsonNode priceNode : pricesNode) {
                JsonNode period = priceNode.get(PERIOD);
                JsonNode cost = priceNode.get(COST);
                String billingCycle = BillingPeriod.getLabelByCode(period.path("$").asText(""));
                String billingCost = cost.path("$").asText("");
                billingCycleMap.put(billingCycle, billingCost);
            }
        }

        return ServerConfig.builder()
                .name(name)
                .description(description)
                .prices(billingCycleMap)
                .currency(currency)
                .serverType("virtual")
                .externalId(externalId)
                .location(datacenterName)
                .serverResources(serverResources)
                .build();
    }

    private void addServerConfig(String baseUrl, String authData, Map<String, String> datacenter,
                                 List<ServerConfig> serverConfigList, String function) throws JsonProcessingException {
        Integer id = Integer.valueOf(datacenter.get("id"));
        String name = datacenter.get("name");

        JsonNode json = getPricingPlansJson(baseUrl, authData, id, function);
        JsonNode servers = json.path("doc").path("list").get(0).path("elem");
        for (JsonNode server : servers) {
            Integer externalId = server.path("id").path("$").asInt();
            ServerConfig serverConfig = parseServerConfigs(externalId, server, name);
            serverConfig.setId(id.toString());
            serverConfigList.add(serverConfig);
        }
    }

    private ServerResources createServerResources(JsonNode server) {
        JsonNode detail = server.has(DETAIL) ? server.get(DETAIL) : NullNode.getInstance();

        String cores = getDetailValue(detail, CORES_FIELD_NAME);
        String ram = getDetailValue(detail, RAM_FIELD_NAME);
        String disk = getDetailValue(detail, DISK_FIELD_NAME);
        String networkSpeed = getDetailValue(detail, NETWORK_SPEED_FIELD_NAME);

        return ServerResources.builder()
                .processorName("")
                .ramType("DDR4")
                .cores(cores)
                .ram(ram)
                .diskType("")
                .disk(disk)
                .coreFrequency("")
                .networkLimit("")
                .networkSpeed(networkSpeed)
                .build();
    }
}
