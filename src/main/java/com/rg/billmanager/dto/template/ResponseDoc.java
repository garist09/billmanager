package com.rg.billmanager.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rg.billmanager.dto.order.OrderElem;
import com.rg.billmanager.dto.order.cart.TotalElem;
import com.rg.billmanager.dto.template.addons.AddonMetadata;
import com.rg.billmanager.dto.template.list.ListItem;
import com.rg.billmanager.dto.template.slist.SListItem;
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
public class ResponseDoc {
    private List<SListItem> slist;
    private List<ListItem> list;
    private AddonMetadata metadata;
    private ValueProperty autoprolong;
    private TotalElem total;
    private List<OrderElem> elem;
    private ValueProperty password;
    private ValueProperty recipe;
    private ValueProperty ostempl;
    private ValueProperty ip;
    @JsonProperty("ipv6_subnet")
    private ValueProperty ipv6Subnet;
    @JsonProperty("createdate")
    private ValueProperty creationDate;
    @JsonProperty("real_expiredate")
    private ValueProperty expirationDate;
    @JsonProperty("remoteid")
    private ValueProperty remoteId;
    private ValueProperty reboot;
    private ValueProperty id;
    private ValueProperty domain;
}
