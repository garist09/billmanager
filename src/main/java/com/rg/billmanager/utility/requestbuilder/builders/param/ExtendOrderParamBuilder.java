package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.config.AuthProperties;
import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.ELID;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.PERIOD;
import static com.rg.billmanager.constants.UrlConstants.SKIP_BASKET;
import static com.rg.billmanager.constants.UrlConstants.SOK;

@Component
@RequiredArgsConstructor
public class ExtendOrderParamBuilder implements RequestParamBuilder<ExtendOrderRequest> {
    private final AuthProperties authProperties;

    @Override
    public RequestType getRequestType() {
        return RequestType.EXTEND_ORDER;
    }

    @Override
    public Map<String, String> buildParams(ExtendOrderRequest request, String function) {
        Map<String, String> params = new HashMap<>();
        String authData = authProperties.getAuthData(request.getBaseUrl());
        params.put(AUTH_INFO, authData);
        params.put(ELID, request.getOrderId());
        params.put(PERIOD, request.getPeriod());
        params.put(FUNC, function);
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
