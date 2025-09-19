package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.ServerActionRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;

@Component
public class ServerActionUrlBuilder implements RequestUrlBuilder<ServerActionRequest> {
    @Override
    public RequestType getRequestType() {
        return RequestType.SERVER_ACTION;
    }

    @Override
    public String buildUrl(ServerActionRequest request, String function) {
        return buildBasicUrl(request.getBaseUrl());
    }
}
