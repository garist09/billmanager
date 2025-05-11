package com.rg.billmanager.contracts.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rg.billmanager.dto.template.addons.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInfoResponse {
    private String login;
    private String password;
    private String ip;
    private String creationDate;
    private String expirationDate;
    private String remoteId;
    private String reboot;
    private String id;
    private String hostname;
    private List<Field> addons;
}
