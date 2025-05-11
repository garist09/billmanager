package com.rg.billmanager.contracts.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfoRequest {
    private String baseUrl;
    private String authData;
    private String id;
    private String hostname;
    private String reboot;
    private Map<String, String> addons;
}
