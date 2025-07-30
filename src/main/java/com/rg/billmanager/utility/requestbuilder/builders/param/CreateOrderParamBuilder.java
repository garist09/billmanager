package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
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
import static com.rg.billmanager.enums.FunctionName.ORDER_PARAM;

@Component
public class CreateOrderParamBuilder implements RequestParamBuilder<CreateOrderRequest> {

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE_ORDER;
    }

    @Override
    public Map<String, String> buildParams(CreateOrderRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, request.getAuthData());
        params.put(ORDER_PERIOD, request.getOrderPeriod());
        params.put(AUTOPROLONG, "off");
        if (request.getExternalId() != null) {
            params.put(PRICELIST, request.getExternalId().toString());
        }
        if (request.getDatacenterId() != null) {
            params.put(DATACENTER, request.getDatacenterId().toString());
        }
        params.put(OSTEMPL, request.getOstempl());
        params.put(RECIPE, request.getRecipe());
        if (request.getOrderCount() != null) {
            params.put(ORDER_COUNT, request.getOrderCount().toString());
        }
        params.put(FUNC, ORDER_PARAM.getName());
        params.put(SOK, "ok");
        params.put(SKIP_BASKET, "on");
        if (request.getRemoteId() != null) {
            params.put(REMOTE_ID, request.getRemoteId());
        }
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
