package com.rg.billmanager.utility.requestbuilder.builders.url;

import com.rg.billmanager.contracts.requests.TokenGenerationRequest;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.PUBLIC_TOKEN_URL;

@Component
public class TokenGenerationUrlBuilder implements RequestUrlBuilder<TokenGenerationRequest> {

    @Override
    public RequestType getRequestType() {
        return RequestType.TOKEN_GENERATION;
    }

    @Override
    public String buildUrl(TokenGenerationRequest request) {
        return UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(request.getBaseUrl())
                .path(PUBLIC_TOKEN_URL)
                .build()
                .toString();
    }
}
