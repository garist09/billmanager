package com.rg.billmanager.dto.template.addons.field_type;

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
public class Slider {
    @JsonProperty("$name")
    private String name;
    @JsonProperty("$min")
    private String min;
    @JsonProperty("$max")
    private String max;
    @JsonProperty("$step")
    private String step;
    @JsonProperty("$cost")
    private String cost;
    @JsonProperty("$period")
    private String period;
    @JsonProperty("$setvalues")
    private String setvalues;
    @JsonProperty("$readonly")
    private String readonly;
}
