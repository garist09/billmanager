package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    CANCELLED("0", "cancelled"),
    WORKING("2", "working"),
    STOPPED("3", "stopped"),
    DELETED("4", "deleted"),
    INSTALLING("5", "installing");

    private final String id;
    private final String name;

    private static final Map<String, String> STATUS_MAP = new HashMap<>();

    static {
        for (ItemStatus itemStatus: values()) {
            STATUS_MAP.put(itemStatus.id, itemStatus.name);
        }
    }

    public static String getStatusNameById(String id) {
        String name = STATUS_MAP.get(id);
        return name != null ? name : "unknown";
    }
}
