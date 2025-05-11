package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.CreateOrderRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderUrlBuilder implements RequestUrlBuilder<CreateOrderRequest> {

    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE_ORDER;
    }

    @Override
    public String buildUrl(CreateOrderRequest request) {
        return buildBasicUrl(request.getBaseUrl());
    }
}
