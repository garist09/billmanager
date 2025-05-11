package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.PricingPlanRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.DATACENTER;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.enums.FunctionName.ORDER_PRICELIST;

@Component
public class PricingPlanUrlBuilder implements RequestUrlBuilder<PricingPlanRequest> {
    @Override
    public RequestType getRequestType() {
        return RequestType.PRICING_PLAN;
    }

    @Override
    public String buildUrl(PricingPlanRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(request.getBaseUrl())
                .path(BILLMGR)
                .queryParam(AUTH_INFO, request.getAuthData())
                .queryParam(FUNC, ORDER_PRICELIST.getName())
                .queryParam(OUT, OutFormat.JSON.getName());

        if (request.getDatacenterId() != null) {
            uriBuilder.queryParam(DATACENTER, request.getDatacenterId());
        }

        return uriBuilder.build().toUriString();
    }
}
