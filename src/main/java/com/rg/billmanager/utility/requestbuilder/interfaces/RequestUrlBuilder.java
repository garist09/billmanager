package com.rg.billmanager.utility.requestbuilder.interfaces;

import com.rg.billmanager.enums.RequestType;
import org.springframework.web.util.UriComponentsBuilder;

import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;

public interface RequestUrlBuilder<T> {
    RequestType getRequestType();
    String buildUrl(T request, String function);

    default String buildBasicUrl(String baseUrl) {
        return UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(baseUrl)
                .path(BILLMGR)
                .build()
                .toString();
    }
}
