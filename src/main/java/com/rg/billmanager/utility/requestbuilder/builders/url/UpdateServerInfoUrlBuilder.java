package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;

@Component
public class UpdateServerInfoUrlBuilder implements RequestUrlBuilder<UpdateServerInfoRequest> {
    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE_SERVER_INFO;
    }

    @Override
    public String buildUrl(UpdateServerInfoRequest request) {
        return buildBasicUrl(request.getBaseUrl());
    }
}
