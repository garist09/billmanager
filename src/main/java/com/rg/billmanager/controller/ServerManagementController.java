package com.rg.billmanager.controller;

import com.rg.billmanager.contracts.requests.ServerActionRequest;
import com.rg.billmanager.service.ServerManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@AllArgsConstructor
@Tag(name = "Server management", description = "Endpoints for managing and performing actions on servers")
public class ServerManagementController {
    private final ServerManagementService serverManagementService;

    @Operation(
            summary = "Perform a server action (e.g., restart, shutdown, start)",
            description = "Triggers a server management operation such as restarting or shutting down a specific server "
                    + "based on the provided action type and server identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Server action executed successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters or unsupported action", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping(path = "/server-action")
    public ResponseEntity<?> restartServer(@RequestBody ServerActionRequest serverActionRequest) throws IOException {
        serverManagementService.performServerAction(serverActionRequest);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "server restarted successfully"
        ));
    }
}
