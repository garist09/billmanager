package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.contracts.requests.ServerActionRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
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
public class ServerActionParamBuilder implements RequestParamBuilder<ServerActionRequest> {

    @Override
    public RequestType getRequestType() {
        return RequestType.SERVER_ACTION;
    }

    @Override
    public Map<String, String> buildParams(ServerActionRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, request.getAuthData());
        params.put(ELID, request.getId());
        params.put(FUNC, request.getFunctionType().getName());
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
