package com.rg.billmanager.utility.requestbuilder;

import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UrlBuilderRegistry {
    private final Map<RequestType, RequestUrlBuilder<?>> builders;

    public UrlBuilderRegistry(List<RequestUrlBuilder<?>> builders) {
        this.builders = builders.stream()
                .collect(Collectors.toMap(RequestUrlBuilder::getRequestType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> RequestUrlBuilder<T> getUrlBuilder(RequestType requestType) {
        return (RequestUrlBuilder<T>) this.builders.get(requestType);
    }
}
