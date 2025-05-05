package com.rg.billmanager.dto.order.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationHeader {
    @JsonProperty("$name")
    private String name;
    @JsonProperty("$")
    private String headerName;
    @JsonProperty("$color")
    private String color;
}
