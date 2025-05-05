package com.rg.billmanager.dto.order.cart.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemConfiguration {
    private String configurationName;
    private String configurationValue;
    private String price;
}
