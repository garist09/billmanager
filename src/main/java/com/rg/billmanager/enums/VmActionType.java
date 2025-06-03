package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VmActionType {
    SERVICE_RESTART("restart"),
    SERVICE_STOP("stop"),
    SERVICE_START("start");

    private final String name;
}
