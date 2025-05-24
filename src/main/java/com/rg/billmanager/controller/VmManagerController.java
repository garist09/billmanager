package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.TokenGenerationRequest;
import com.rg.billmanager.service.VmManagerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class VmManagerController {
    private final VmManagerService vmManagerService;

    @PostMapping(path = "/generate-token")
    public ResponseEntity<String> generateToken(@RequestBody TokenGenerationRequest tokenGenerationRequest)
            throws JsonProcessingException {
        return ResponseEntity.ok(vmManagerService.generatePublicToken(tokenGenerationRequest));
    }
}
