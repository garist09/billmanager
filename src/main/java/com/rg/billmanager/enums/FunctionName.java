package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FunctionName {
    ORDER_PARAM("v2.vds.order.param"),
    ORDER_PRICELIST("v2.vds.order.pricelist"),
    CART("cart"),
    VDS("vds"),
    SERVICE_PROLONG("service.prolong");

    private final String name;
}
