package com.rg.billmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerResources {
    private String processorName;
    private String ramType;
    private Integer cores;
    private Integer ram;
    private String diskType;
    private Integer disk;
    private String coreFrequency;
    private Double traffic;
    private Integer networkSpeed;
}
