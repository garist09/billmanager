package com.rg.billmanager.dto.template.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rg.billmanager.dto.template.ValueProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceDetails {
    private ValueProperty cost;
    private ValueProperty currency;
    private ValueProperty discount;
    private ValueProperty discountPercent;
    private ValueProperty noDiscountCost;
    // for discount_summary
    private ValueProperty sign;
}
