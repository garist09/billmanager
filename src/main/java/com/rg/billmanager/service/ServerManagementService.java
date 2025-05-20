package com.rg.billmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.ServerActionRequest;

public interface ServerManagementService {
    void performServerAction(ServerActionRequest serverActionRequest) throws JsonProcessingException;
}
