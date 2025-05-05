package com.rg.billmanager.dto.template.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rg.billmanager.dto.order.cart.ActionContainer;
import com.rg.billmanager.dto.order.cart.ConfigurationContainer;
import com.rg.billmanager.dto.order.cart.MainElem;
import com.rg.billmanager.dto.order.cart.PriceContainer;
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
public class ListElem {
    private CostElem cost;
    private LabelElem label;
    @JsonProperty("price")
    private PriceContainer priceContainer;
    @JsonProperty("configuration")
    private ConfigurationContainer configurationContainer;
    @JsonProperty("action")
    private ActionContainer actionContainer;
    private MainElem main;
    private ValueProperty elid;
    private ValueProperty id;
}
