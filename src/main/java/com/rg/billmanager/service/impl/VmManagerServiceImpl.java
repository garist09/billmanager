package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.contracts.requests.TokenGenerationRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.service.VmManagerService;
import com.rg.billmanager.utility.requestbuilder.UrlBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.rg.billmanager.constants.JsonFields.TOKEN;
import static com.rg.billmanager.constants.UrlConstants.EMAIL;
import static com.rg.billmanager.constants.UrlConstants.PASSWORD;

@Service
@RequiredArgsConstructor
public class VmManagerServiceImpl implements VmManagerService {
    private final RestTemplate restTemplate;
    private final UrlBuilderRegistry urlBuilderRegistry;
    private final ObjectMapper objectMapper;


    @Override
    public String generatePublicToken(TokenGenerationRequest tokenGenerationRequest) throws JsonProcessingException {
        RequestUrlBuilder<TokenGenerationRequest> urlBuilder = urlBuilderRegistry
                .getUrlBuilder(RequestType.TOKEN_GENERATION);

        String newUrl = urlBuilder.buildUrl(tokenGenerationRequest, null);

        Map<String, String> body = new HashMap<>();
        body.put(EMAIL, tokenGenerationRequest.getEmail());
        body.put(PASSWORD, tokenGenerationRequest.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(newUrl, request, String.class);
        String responseBody = response.getBody();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        return rootNode.get(TOKEN).asText();
    }
}
