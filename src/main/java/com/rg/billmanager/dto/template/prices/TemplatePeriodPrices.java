package com.rg.billmanager.dto.template.prices;

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
public class TemplatePeriodPrices {
    private Map<String, List<TemplatePrice>> periodPricesMap;
}
