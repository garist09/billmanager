package com.rg.billmanager.contracts.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PricingPlanRequest {
    private String baseUrl;
    private String authData;
    private Integer datacenterId;
}
