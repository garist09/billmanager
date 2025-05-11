package com.rg.billmanager.contracts.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusRequest {
    private String baseUrl;
    private String authData;
    private List<String> remoteIds;
}
