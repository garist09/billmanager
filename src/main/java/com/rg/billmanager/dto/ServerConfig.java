package com.rg.billmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerConfig {
    private String name;
    private String location;
    private String description;
    private Map<String, String> prices;
    private String currency;
    private String serverType;
    private Integer providerId;
    private Integer externalId;
    private ServerResources serverResources;
}
