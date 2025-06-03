package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ServerActionRequest;
import com.rg.billmanager.exception_handler.exception.InvalidCreationException;
import com.rg.billmanager.service.ServerManagementService;
import com.rg.billmanager.utility.ErrorUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.rg.billmanager.constants.UrlConstants.FORCE;
import static com.rg.billmanager.constants.UrlConstants.XSRF_TOKEN;

@Service
@RequiredArgsConstructor
public class ServerManagementServiceImpl implements ServerManagementService {
    private final RestTemplate restTemplate;
    private final ErrorUtility errorUtility;

    @Override
    public void performServerAction(ServerActionRequest serverActionRequest) throws JsonProcessingException {
        String url = String.format("https://spacecore.cloud/vm/v3/host/%d/%s", serverActionRequest.getId(),
                serverActionRequest.getFunctionType().getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(XSRF_TOKEN, serverActionRequest.getToken());
        Map<String, Object> body = Map.of(FORCE, serverActionRequest.getForce());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String response = restTemplate.postForEntity(url, request, String.class).getBody();

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidCreationException(error);
        }
    }
}
