package com.rg.billmanager.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusResponse {
    private String remoteId;
    private String orderStatusId;
    private String orderStatus;
}
