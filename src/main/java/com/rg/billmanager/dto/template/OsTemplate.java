package com.rg.billmanager.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class OsTemplate {
    private String id;
    private String name;
    private String family;
    private Double cost;
    private List<AppTemplate> appTemplateList;

    public void addAppTemplate(AppTemplate appTemplate) {
        appTemplateList.add(appTemplate);
    }
}
