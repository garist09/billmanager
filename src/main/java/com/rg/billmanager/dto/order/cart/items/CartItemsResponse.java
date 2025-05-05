package com.rg.billmanager.dto.order.cart.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsResponse {
    private List<CartItem> cartItemList;
    private CartTotal total;
}
