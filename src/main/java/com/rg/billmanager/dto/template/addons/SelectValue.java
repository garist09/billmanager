package com.rg.billmanager.dto.template.addons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectValue {
    private String key;
    private String value;
    private String price;
}
