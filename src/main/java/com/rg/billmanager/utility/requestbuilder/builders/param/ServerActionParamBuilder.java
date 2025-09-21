package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.config.AuthProperties;
import com.rg.billmanager.contracts.requests.ServerActionRequest;
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
import static com.rg.billmanager.constants.UrlConstants.SKIP_BASKET;
import static com.rg.billmanager.constants.UrlConstants.SOK;

@Component
@RequiredArgsConstructor
public class ServerActionParamBuilder implements RequestParamBuilder<ServerActionRequest> {
    private final AuthProperties authProperties;

    @Override
    public RequestType getRequestType() {
        return RequestType.SERVER_ACTION;
    }

    @Override
    public Map<String, String> buildParams(ServerActionRequest request, String function) {
        Map<String, String> params = new HashMap<>();
        String authData = authProperties.getAuthData(request.getBaseUrl());
        params.put(AUTH_INFO, authData);
        params.put(ELID, request.getId());
        params.put(FUNC, function);
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
