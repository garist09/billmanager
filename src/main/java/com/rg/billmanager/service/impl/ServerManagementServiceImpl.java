package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ServerActionRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.exception_handler.exception.InvalidCreationException;
import com.rg.billmanager.service.ServerManagementService;
import com.rg.billmanager.utility.ErrorUtility;
import com.rg.billmanager.utility.UrlUtils;
import com.rg.billmanager.utility.requestbuilder.ParamBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.UrlBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServerManagementServiceImpl implements ServerManagementService {
    private final RestTemplate restTemplate;
    private final UrlBuilderRegistry urlBuilderRegistry;
    private final ParamBuilderRegistry paramBuilderRegistry;
    private final ErrorUtility errorUtility;

    @Override
    public void performServerAction(ServerActionRequest serverActionRequest) throws JsonProcessingException {
        RequestUrlBuilder<ServerActionRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.SERVER_ACTION);
        RequestParamBuilder<ServerActionRequest> paramBuilder = paramBuilderRegistry
                .getParamBuilder(RequestType.SERVER_ACTION);

        String url = urlBuilder.buildUrl(serverActionRequest, null);
        Map<String, String> params = paramBuilder.buildParams(serverActionRequest);

        HttpEntity<String> request = UrlUtils.buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidCreationException(error);
        }
    }
}
