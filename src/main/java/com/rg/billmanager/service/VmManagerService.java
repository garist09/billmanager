package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.TokenGenerationRequest;

public interface VmManagerService {
    String generatePublicToken(TokenGenerationRequest tokenGenerationRequest) throws JsonProcessingException;
}
