package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
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
import static com.rg.billmanager.constants.UrlConstants.PERIOD;
import static com.rg.billmanager.constants.UrlConstants.SKIP_BASKET;
import static com.rg.billmanager.constants.UrlConstants.SOK;
import static com.rg.billmanager.enums.FunctionName.SERVICE_PROLONG;

@Component
public class ExtendOrderParamBuilder implements RequestParamBuilder<ExtendOrderRequest> {

    @Override
    public RequestType getRequestType() {
        return RequestType.EXTEND_ORDER;
    }

    @Override
    public Map<String, String> buildParams(ExtendOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, request.getAuthData());
        params.put(ELID, request.getOrderId());
        params.put(PERIOD, request.getPeriod());
        params.put(FUNC, SERVICE_PROLONG.getName());
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
