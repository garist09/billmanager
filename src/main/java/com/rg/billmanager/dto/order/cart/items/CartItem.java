package com.rg.billmanager.dto.order.cart.items;

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
public class CartItem {
    private String id;
    private String elid;
    private String name;
    private String period;
    private String price;
    private List<ItemConfiguration> configurationList;
    private ItemAction action;
}
