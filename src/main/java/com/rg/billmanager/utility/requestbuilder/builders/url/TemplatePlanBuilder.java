package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.TemplatePlanRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rg.billmanager.constants.JsonFields.PERIOD;
import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.PRICELIST;

@Component
public class TemplatePlanBuilder implements RequestUrlBuilder<TemplatePlanRequest> {
    @Override
    public RequestType getRequestType() {
        return RequestType.TEMPLATE_PLAN;
    }

    @Override
    public String buildUrl(TemplatePlanRequest request, String function) {
        return UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(request.getBaseUrl())
                .path(BILLMGR)
                .queryParam(AUTH_INFO, request.getAuthData())
                .queryParam(FUNC, function)
                .queryParam(PRICELIST, request.getExternalId())
                .queryParam(PERIOD, request.getPeriod())
                .queryParam(OUT, OutFormat.JSON.getName())
                .build().toString();
    }
}
