package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VmActionType {
    SERVICE_REBOOT("service.reboot"),
    SERVICE_STOP("service.stop"),
    SERVICE_START("service.start");

    private final String name;
}
