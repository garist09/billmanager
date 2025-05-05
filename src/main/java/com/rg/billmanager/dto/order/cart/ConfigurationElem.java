package com.rg.billmanager.dto.order.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ConfigurationElem {
    @JsonProperty("configuration_name")
    private ValueProperty configurationName;
    @JsonProperty("configuration_value")
    private ValueProperty configurationValue;
    @JsonProperty("configuration_price")
    private PriceContainer configurationPrice;
}
