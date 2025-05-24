package com.rg.billmanager.contracts.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenGenerationRequest {
    private String baseUrl;
    private String email;
    private String password;
}
