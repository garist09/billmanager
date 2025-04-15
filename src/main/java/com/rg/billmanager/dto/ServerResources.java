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
    private String cores;
    private String ram;
    private String diskType;
    private String disk;
    private String coreFrequency;
    private String networkLimit;
    private String networkSpeed;
}
