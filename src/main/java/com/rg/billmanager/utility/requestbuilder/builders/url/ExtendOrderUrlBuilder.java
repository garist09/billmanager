package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.ExtendOrderRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExtendOrderUrlBuilder implements RequestUrlBuilder<ExtendOrderRequest> {

    @Override
    public RequestType getRequestType() {
        return RequestType.EXTEND_ORDER;
    }

    @Override
    public String buildUrl(ExtendOrderRequest request) {
        return buildBasicUrl(request.getBaseUrl());
    }
}
