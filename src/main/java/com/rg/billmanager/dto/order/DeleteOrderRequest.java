package com.rg.billmanager.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteOrderRequest {
    private String baseUrl;
    private String authData;
    private Integer cartItemId;
}
