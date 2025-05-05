package com.rg.billmanager.mapper;

import com.rg.billmanager.dto.order.cart.ActionContainer;
import com.rg.billmanager.dto.order.cart.ConfigurationContainer;
import com.rg.billmanager.dto.order.cart.ConfigurationElem;
import com.rg.billmanager.dto.order.cart.MainElem;
import com.rg.billmanager.dto.order.cart.PriceContainer;
import com.rg.billmanager.dto.order.cart.items.CartItem;
import com.rg.billmanager.dto.order.cart.items.CartTotal;
import com.rg.billmanager.dto.order.cart.items.ItemAction;
import com.rg.billmanager.dto.order.cart.items.ItemConfiguration;
import com.rg.billmanager.dto.template.DocumentResponse;
import com.rg.billmanager.dto.template.ResponseDoc;
import com.rg.billmanager.dto.template.list.ListElem;
import com.rg.billmanager.dto.template.list.ListItem;
import com.rg.billmanager.dto.template.list.PriceDetails;
import com.rg.billmanager.utility.OptionalUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartResponseMapper {
    private static final String LINEITEMS_FIELD = "lineitems";

    public CartTotal parseCartTotal(DocumentResponse documentResponse) {
        return Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getTotal)
                .map(total -> {
                    String cost = OptionalUtility.safeGet(total.getCost());
                    String currency = OptionalUtility.safeGet(total.getCurrency());
                    String count = OptionalUtility.safeGet(total.getCount());
                    return CartTotal.builder()
                            .price(cost + currency)
                            .count(count)
                            .build();
                })
                .orElse(null);
    }

    public List<CartItem> parseCartItems(DocumentResponse documentResponse) {
        return Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(elem -> LINEITEMS_FIELD.equals(Optional.ofNullable(elem.getName()).orElse("")))
                .map(ListItem::getElem)
                .flatMap(List::stream)
                .map(elem -> {
                    List<ItemConfiguration> itemConfigurationList = extractItemConfigurations(elem);
                    ItemAction itemAction = extractItemAction(elem);
                    return createCartItem(elem, itemConfigurationList, itemAction);
                })
                .toList();
    }

    private List<ItemConfiguration> extractItemConfigurations(ListElem elem) {
        return Optional.ofNullable(elem.getConfigurationContainer())
                .map(ConfigurationContainer::getElem)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::createItemConfiguration)
                .collect(Collectors.toList());
    }

    private ItemAction extractItemAction(ListElem elem) {
        return Optional.ofNullable(elem.getActionContainer())
                .map(ActionContainer::getButton)
                .map(button -> ItemAction.builder()
                        .name(button.getName())
                        .type(button.getType())
                        .functionName(button.getFunctionName())
                        .build())
                .orElse(null);
    }


    private ItemConfiguration createItemConfiguration(ConfigurationElem configurationElem) {
        PriceDetails priceDetails = Optional.ofNullable(configurationElem.getConfigurationPrice())
                .map(PriceContainer::getPrice)
                .orElse(null);

        String configurationName = OptionalUtility.safeGet(configurationElem.getConfigurationName());
        configurationName = configurationName.replaceAll("(?i)&nbsp;[-–—]?&nbsp;", "").trim();
        String configurationValue = OptionalUtility.safeGet(configurationElem.getConfigurationValue());
        String cost = OptionalUtility.safeExtract(PriceDetails::getCost, priceDetails);
        String currency = OptionalUtility.safeExtract(PriceDetails::getCurrency, priceDetails);

        return new ItemConfiguration(configurationName, configurationValue, cost + currency);
    }

    private CartItem createCartItem(ListElem elem, List<ItemConfiguration> itemConfigurationList,
                                    ItemAction itemAction) {
        PriceDetails priceDetails = Optional.ofNullable(elem.getPriceContainer())
                .map(PriceContainer::getPrice)
                .orElse(null);

        String id = OptionalUtility.safeGet(elem.getId());
        String elid = OptionalUtility.safeGet(elem.getElid());
        String name = OptionalUtility.safeExtract(MainElem::getName, elem.getMain());
        String period = OptionalUtility.safeExtract(MainElem::getPeriod, elem.getMain());
        String cost = OptionalUtility.safeExtract(PriceDetails::getCost, priceDetails);
        String currency = OptionalUtility.safeExtract(PriceDetails::getCurrency, priceDetails);

        return CartItem.builder()
                .id(id)
                .elid(elid)
                .name(name)
                .period(period)
                .price(cost + currency)
                .configurationList(itemConfigurationList)
                .action(itemAction).build();
    }
}
