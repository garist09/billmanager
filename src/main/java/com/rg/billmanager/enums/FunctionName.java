package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FunctionName {
    VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM("v2.vds.order.param"),
    DEDICATED_SERVERS_ORDER_PARAM("v2.dedic.order.param"),
    VIRTUAL_PRIVATE_SERVERS_PRICELIST("v2.vds.order.pricelist"),
    DEDICATED_SERVERS_PRICELIST("v2.dedic.order.pricelist"),
    CART("cart"),
    VDS("vds"),
    SERVICE_PROLONG("service.prolong"),
    VDS_EDIT("vds.edit"),
    SERVICE_REBOOT("service.reboot");

    private final String name;
}
