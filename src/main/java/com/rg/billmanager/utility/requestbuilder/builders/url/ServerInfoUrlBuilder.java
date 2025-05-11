package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.ServerInfoRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.ELID;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.enums.FunctionName.VDS_EDIT;

@Component
public class ServerInfoUrlBuilder implements RequestUrlBuilder<ServerInfoRequest> {
    @Override
    public RequestType getRequestType() {
        return RequestType.SERVER_INFO;
    }

    @Override
    public String buildUrl(ServerInfoRequest request) {
        return UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(request.getBaseUrl())
                .path(BILLMGR)
                .queryParam(AUTH_INFO, request.getAuthData())
                .queryParam(FUNC, VDS_EDIT.getName())
                .queryParam(ELID, request.getOrderId())
                .queryParam(OUT, OutFormat.XJSON.getName())
                .build().toString();
    }
}
