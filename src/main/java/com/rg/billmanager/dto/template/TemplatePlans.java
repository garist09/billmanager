package com.rg.billmanager.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rg.billmanager.dto.template.addons.Field;
import com.rg.billmanager.dto.template.prices.TemplatePeriodPrices;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplatePlans {
    private Map<String, List<OsTemplate>> groupedTemplates;
    private List<Field> addons;
    private Map<String, TemplatePeriodPrices> templatePeriodPrices;
    private String autoprolong;
}
