package com.rg.billmanager.dto.template.addons.field_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rg.billmanager.dto.template.addons.SelectValue;
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
public class Select {
    @JsonProperty("$name")
    private String name;
    @JsonProperty("$setvalues")
    private String setValues;
    private List<SelectValue> selectValues;
}
