package com.rg.billmanager.utility.requestbuilder;

import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParamBuilderRegistry {
    private final Map<RequestType, RequestParamBuilder<?>> builders;

    public ParamBuilderRegistry(List<RequestParamBuilder<?>> builders) {
        this.builders = builders.stream()
                .collect(Collectors.toMap(RequestParamBuilder::getRequestType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> RequestParamBuilder<T> getParamBuilder(RequestType type) {
        return (RequestParamBuilder<T>) builders.get(type);
    }
}
