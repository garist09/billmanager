package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutFormat {
    JSON("json"),
    XJSON("xjson");

    private final String name;
}
