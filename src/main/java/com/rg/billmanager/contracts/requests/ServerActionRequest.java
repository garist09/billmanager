package com.rg.billmanager.contracts.requests;

import com.rg.billmanager.enums.VmActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerActionRequest {
    private String baseUrl;
    private Integer id;
    private String token;
    private Boolean force;
    private VmActionType functionType;
}
