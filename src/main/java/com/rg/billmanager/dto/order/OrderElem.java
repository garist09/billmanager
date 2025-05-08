package com.rg.billmanager.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rg.billmanager.dto.template.ValueProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderElem {
    @JsonProperty("remoteid")
    private ValueProperty remoteId;
    private ValueProperty status;
    @JsonProperty("item_status")
    private ItemStatusElem itemStatus;
}
