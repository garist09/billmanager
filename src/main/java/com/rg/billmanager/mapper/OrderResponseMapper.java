package com.rg.billmanager.mapper;

import com.rg.billmanager.dto.order.ItemStatusElem;
import com.rg.billmanager.dto.order.OrderStatusResponse;
import com.rg.billmanager.dto.template.DocumentResponse;
import com.rg.billmanager.dto.template.ResponseDoc;
import com.rg.billmanager.utility.OptionalUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderResponseMapper {

    public List<OrderStatusResponse> parseOrderStatuses(DocumentResponse documentResponse, List<String> orderIds) {
        return Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getElem)
                .orElse(Collections.emptyList())
                .stream()
                .filter(elem -> elem.getRemoteId() != null && orderIds.contains(elem.getRemoteId().getValue()))
                .map(elem -> {
                    String remoteId = OptionalUtility.safeGet(elem.getRemoteId());
                    String orderStatusId = OptionalUtility.safeGet(elem.getStatus());
                    String orderStatusName = Optional.ofNullable(elem.getItemStatus())
                            .map(ItemStatusElem::getStatusName)
                            .orElse("");

                    return new OrderStatusResponse(remoteId, orderStatusId, orderStatusName);
                })
                .toList();
    }
}
