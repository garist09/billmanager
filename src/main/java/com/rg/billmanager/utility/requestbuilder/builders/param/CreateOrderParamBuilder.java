package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.config.AuthProperties;
import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.AUTOPROLONG;
import static com.rg.billmanager.constants.UrlConstants.DATACENTER;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.ORDER_COUNT;
import static com.rg.billmanager.constants.UrlConstants.ORDER_PERIOD;
import static com.rg.billmanager.constants.UrlConstants.OSTEMPL;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.PRICELIST;
import static com.rg.billmanager.constants.UrlConstants.RECIPE;
import static com.rg.billmanager.constants.UrlConstants.REMOTE_ID;
import static com.rg.billmanager.constants.UrlConstants.SKIP_BASKET;
import static com.rg.billmanager.constants.UrlConstants.SOK;

@Component
@RequiredArgsConstructor
public class CreateOrderParamBuilder implements RequestParamBuilder<CreateOrderRequest> {
    private final AuthProperties authProperties;

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE_ORDER;
    }

    @Override
    public Map<String, String> buildParams(CreateOrderRequest request, String function) {
        Map<String, String> params = new HashMap<>();
        String authData = authProperties.getAuthData(request.getBaseUrl());
        params.put(AUTH_INFO, authData);
        params.put(ORDER_PERIOD, request.getOrderPeriod());
        params.put(AUTOPROLONG, request.getAutoprolong());
        if (request.getExternalId() != null) {
            params.put(PRICELIST, request.getExternalId().toString());
        }
        if (request.getDatacenterId() != null) {
            params.put(DATACENTER, request.getDatacenterId().toString());
        }
        params.put(OSTEMPL, request.getOstempl());
        if (request.getRecipe() != null) {
            params.put(RECIPE, request.getRecipe());
        }
        if (request.getOrderCount() != null) {
            params.put(ORDER_COUNT, request.getOrderCount().toString());
        }
        params.put(FUNC, function);
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        if (request.getRemoteId() != null) {
            params.put(REMOTE_ID, request.getRemoteId());
        }
        if (request.getAddons() != null) {
            params.putAll(request.getAddons());
        }
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
