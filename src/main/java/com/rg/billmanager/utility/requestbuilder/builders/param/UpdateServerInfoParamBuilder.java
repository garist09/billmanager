package com.rg.billmanager.utility.requestbuilder.builders.param;

import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.DOMAIN;
import static com.rg.billmanager.constants.UrlConstants.ELID;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.REBOOT;
import static com.rg.billmanager.constants.UrlConstants.SOK;
import static com.rg.billmanager.enums.FunctionName.VDS_EDIT;

@Component
public class UpdateServerInfoParamBuilder implements RequestParamBuilder<UpdateServerInfoRequest> {
    @Override
    public RequestType getRequestType() {
        return RequestType.UPDATE_SERVER_INFO;
    }

    @Override
    public Map<String, String> buildParams(UpdateServerInfoRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, request.getAuthData());
        params.put(ELID, request.getId());
        params.put(DOMAIN, request.getHostname());
        params.put(REBOOT, request.getReboot());
        params.put(FUNC, VDS_EDIT.getName());
        request.getAddons().entrySet().forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        params.put(SOK, "ok");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }
}
