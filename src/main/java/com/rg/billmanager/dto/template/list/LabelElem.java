package com.rg.billmanager.dto.template.list;

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
public class LabelElem {
    @JsonProperty("$")
    private String value;
    @JsonProperty("$color")
    private String color;
    @JsonProperty("$size")
    private String size;
    @JsonProperty("$weight")
    private String weight;
}
