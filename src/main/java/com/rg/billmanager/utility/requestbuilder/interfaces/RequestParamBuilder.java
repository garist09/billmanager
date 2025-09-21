package com.rg.billmanager.utility.requestbuilder.interfaces;

import com.rg.billmanager.enums.RequestType;

import java.util.Map;

public interface RequestParamBuilder<T> {
    RequestType getRequestType();
    Map<String, String> buildParams(T request, String function);
}
