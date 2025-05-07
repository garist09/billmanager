package com.rg.billmanager.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String baseUrl;
    private String authData;
    private Integer externalId;
    private Integer datacenterId;
    private Integer remoteId;
    private String orderPeriod;
    // operation system
    private String ostempl;
    // application
    private String recipe;
    private String autoprolong;
    private Integer orderCount;
}
