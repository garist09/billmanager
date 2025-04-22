package com.rg.billmanager.dto.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
}
