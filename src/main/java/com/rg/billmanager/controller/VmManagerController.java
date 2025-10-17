package com.rg.billmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rg.billmanager.contracts.requests.TokenGenerationRequest;
import com.rg.billmanager.service.VmManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "VM Manager", description = "Endpoints for managing virtual machine operations and token generation")
public class VmManagerController {
    private final VmManagerService vmManagerService;

    @Operation(
            summary = "Generate a public VM management token",
            description = "Generates a secure, time-limited token that allows external systems or users "
                    + "to access virtual machine management interfaces.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token generated successfully",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(description = "Generated public token string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))),
                    @ApiResponse(responseCode = "400", description = "Invalid token generation request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping(path = "/generate-token")
    public ResponseEntity<String> generateToken(@RequestBody TokenGenerationRequest tokenGenerationRequest)
            throws JsonProcessingException {
        return ResponseEntity.ok(vmManagerService.generatePublicToken(tokenGenerationRequest));
    }
}
