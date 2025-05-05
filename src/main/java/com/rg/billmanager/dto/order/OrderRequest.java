package com.rg.billmanager.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String baseUrl;
    private String authData;
    private Integer externalId;
    private String orderPeriod;
    // operation system
    private String ostempl;
    // application
    private String recipe;
    private Map<String, String> addons;
    private String autoprolong;
    private Integer orderCount;
}
