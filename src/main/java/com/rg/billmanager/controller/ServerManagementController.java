package com.rg.billmanager.controller;

import com.rg.billmanager.contracts.requests.ServerActionRequest;
import com.rg.billmanager.service.ServerManagementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ServerManagementController {
    private final ServerManagementService serverManagementService;

    @PostMapping(path = "/server-action")
    public ResponseEntity<?> restartServer(@RequestBody ServerActionRequest serverActionRequest) throws IOException {
        serverManagementService.performServerAction(serverActionRequest);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "server restarted successfully"
        ));
    }
}
