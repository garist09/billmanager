package com.rg.billmanager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FunctionName {
    VIRTUAL_PRIVATE_SERVERS_ORDER_PARAM("v2.vds.order.param"),
    DEDICATED_SERVERS_ORDER_PARAM("v2.dedic.order.param"),
    SERVER_AUCTION_ORDER_PARAM("v2.not-install.order.param"),
    VIRTUAL_PRIVATE_SERVERS_PRICELIST("v2.vds.order.pricelist"),
    DEDICATED_SERVERS_PRICELIST("v2.dedic.order.pricelist"),
    SERVER_AUCTION_PRICELIST("v2.not-install.order.pricelist"),
    CART("cart"),
    VDS("vds"),
    SERVICE_PROLONG("service.prolong"),
    VIRTUAL_PRIVATE_SERVER_EDIT("vds.edit"),
    DEDICATED_SERVER_EDIT("dedic.edit"),
    SERVER_AUCTION_EDIT("not-install.edit"),
    SERVICE_REBOOT("service.reboot");

    private final String name;
}
