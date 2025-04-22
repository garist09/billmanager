package com.rg.billmanager.dto.template.prices;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplatePrice {
    private String label;
    private String cost;
    private String noDiscountCost;
}
