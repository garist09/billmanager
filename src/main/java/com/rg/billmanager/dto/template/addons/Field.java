package com.rg.billmanager.dto.template.addons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rg.billmanager.dto.template.addons.field_type.Input;
import com.rg.billmanager.dto.template.addons.field_type.Select;
import com.rg.billmanager.dto.template.addons.field_type.Slider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
    @JsonProperty("$name")
    private String name;
    @JsonProperty("slider")
    private List<Slider> sliders;
    @JsonProperty("input")
    private List<Input> inputs;
    @JsonProperty("select")
    private List<Select> selects;
    private String addonName;
    private String addonHintName;
    private String addonValue;
}
