package com.rg.billmanager.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExtendOrderRequest {
    private String baseUrl;
    private String authData;
    private String orderId;
    private String period;
}
