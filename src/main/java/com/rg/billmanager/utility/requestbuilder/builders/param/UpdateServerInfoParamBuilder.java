package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.config.AuthProperties;
import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.DOMAIN;
import static com.rg.billmanager.constants.UrlConstants.ELID;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.REBOOT;
import static com.rg.billmanager.constants.UrlConstants.SOK;

@Component
@RequiredArgsConstructor
public class UpdateServerInfoParamBuilder implements RequestParamBuilder<UpdateServerInfoRequest> {
    private final AuthProperties authProperties;

    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE_SERVER_INFO;
    }

    @Override
    public Map<String, String> buildParams(UpdateServerInfoRequest request, String function) {
        Map<String, String> params = new HashMap<>();
        String authData = authProperties.getAuthData(request.getBaseUrl());
        params.put(AUTH_INFO, authData);
        params.put(ELID, request.getId());
        params.put(DOMAIN, request.getHostname());
        params.put(REBOOT, request.getReboot());
        params.put(FUNC, function);
        request.getAddons().entrySet().forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        params.put(SOK, "ok");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
