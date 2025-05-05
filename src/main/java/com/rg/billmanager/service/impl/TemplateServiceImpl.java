package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.dto.template.AppTemplate;
import com.rg.billmanager.dto.template.DocumentResponse;
import com.rg.billmanager.dto.template.ResponseDoc;
import com.rg.billmanager.dto.template.TemplatePlans;
import com.rg.billmanager.dto.template.addons.AddonMetadata;
import com.rg.billmanager.dto.template.addons.Field;
import com.rg.billmanager.dto.template.addons.FormMetadata;
import com.rg.billmanager.dto.template.addons.Page;
import com.rg.billmanager.dto.template.prices.TemplatePeriodPrices;
import com.rg.billmanager.dto.template.prices.TemplatePrice;
import com.rg.billmanager.dto.template.ValueProperty;
import com.rg.billmanager.dto.template.list.CostElem;
import com.rg.billmanager.dto.template.list.LabelElem;
import com.rg.billmanager.dto.template.list.ListElem;
import com.rg.billmanager.dto.template.list.PriceDetails;
import com.rg.billmanager.dto.template.slist.SListItem;
import com.rg.billmanager.dto.template.OsTemplate;
import com.rg.billmanager.dto.template.list.ListItem;
import com.rg.billmanager.enums.BillingPeriod;
import com.rg.billmanager.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TemplatePlans getTemplatesForPlans(String baseUrl, String authData, Integer externalId)
            throws JsonProcessingException {
        String url = String.format("https://%s/billmgr?authinfo=%s&func=v2.vds.order.param&pricelist=%s&period=1&out=json",
                baseUrl, authData, externalId);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        Map<String, List<OsTemplate>> groupedTemplates = getGroupedTemplates(documentResponse);

        List<Field> fieldList = getAddons(response, documentResponse);

        Map<String, TemplatePeriodPrices> templatePeriodPricesMap = getAllTemplatesPrices(baseUrl, authData, externalId);

        String autoprolong = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(doc -> doc.getAutoprolong().getValue())
                .orElse("");

        return new TemplatePlans(groupedTemplates, fieldList, templatePeriodPricesMap, autoprolong);
    }

    private Map<String, List<OsTemplate>> getGroupedTemplates(DocumentResponse documentResponse) {
        Map<String, Object> templates = new HashMap<>();
        List<OsTemplate> osTemplateList = new ArrayList<>();
        Map<String, List<OsTemplate>> groupedTemplatesMap = new HashMap<>();

        List<SListItem> sListItems = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getSlist)
                .orElse(new ArrayList<>());

        for (SListItem sListItem : sListItems) {
            if (sListItem.getName().equals("ostempl")) {
                groupedTemplatesMap = getOperationSystems(templates, osTemplateList, sListItem);
            }
            if (sListItem.getName().equals("recipe")) {
                addApplicationToOperationSystems(templates, groupedTemplatesMap, sListItem);
            }
        }

        return groupedTemplatesMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(OsTemplate::getFamily));
    }

    private List<Field> getAddons(ResponseEntity<String> response, DocumentResponse documentResponse)
            throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        List<Page> metadataPages = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getMetadata)
                .map(AddonMetadata::getForm)
                .map(FormMetadata::getPages)
                .orElse(new ArrayList<>());

        return metadataPages.stream().map(Page::getFields)
                .flatMap(fields -> fields.stream())
                .filter(field -> field.getName().contains("addon_"))
                .peek(field -> {
                    JsonNode messagesNode = jsonNode.path("doc").path("messages").path("msg");
                    field.setAddonName(messagesNode.path(field.getName()).toString());
                    field.setAddonHintName(messagesNode.path("hint_" + field.getName()).toString());
                })
                .toList();
    }

    private Map<String, TemplatePeriodPrices> getAllTemplatesPrices(String baseUrl, String authData, Integer externalId)
            throws JsonProcessingException {
        List<String> billingPeriods = List.of(BillingPeriod.DAILY.getCode(), BillingPeriod.MONTHLY.getCode(),
                BillingPeriod.QUARTERLY.getCode(), BillingPeriod.SEMI_ANNUAL.getCode(), BillingPeriod.ANNUAL.getCode());
        Map<String, TemplatePeriodPrices> templatePricesByPeriods = new HashMap<>();

        for (String billingPeriod : billingPeriods) {
            templatePricesByPeriods.put(billingPeriod, getTemplatesPricesForPeriod(baseUrl, authData, externalId,
                    billingPeriod));
        }

        return templatePricesByPeriods;
    }

    private Map<String, List<OsTemplate>> getOperationSystems(Map<String, Object> templates,
                                                              List<OsTemplate> osTemplateList, SListItem sListItem) {
        for (Map<String, Object> values : sListItem.getVal()) {
            String osId = (String) values.getOrDefault("$key", "");
            OsTemplate osTemplate = new OsTemplate((String) values.getOrDefault("$key", ""),
                    (String) values.getOrDefault("$", ""),
                    (String) values.getOrDefault("$valuegroup", ""),
                    new ArrayList<>());
            osTemplateList.add(osTemplate);
            templates.putIfAbsent(osId, osTemplate);
        }
        return osTemplateList.stream().collect(Collectors.groupingBy(OsTemplate::getId));
    }

    private void addApplicationToOperationSystems(Map<String, Object> templates,
                                                  Map<String, List<OsTemplate>> groupedTemplatesMap,
                                                  SListItem sListItem) {
        for (Map<String, Object> values : sListItem.getVal()) {
            if (values.get("$key") != null && values.containsKey("$depend")) {
                String osId = (String) values.getOrDefault("$depend", "");
                String appId = (String) values.getOrDefault("$key", "");
                String appName = (String) values.getOrDefault("$", "");
                AppTemplate appTemplate = new AppTemplate(osId, appId, appName);
                if (groupedTemplatesMap.containsKey(osId)) {
                    groupedTemplatesMap.get(osId).get(0).addAppTemplate(appTemplate);
                }
                templates.putIfAbsent(osId, appTemplate);
            }
        }
    }

    private ArrayList<TemplatePrice> getTemplatePricesList(ListItem listItem) {
        ArrayList<TemplatePrice> templatePriceList = new ArrayList<>();

        if (listItem.getElem() == null) {
            return new ArrayList<>();
        }

        for (ListElem listElem : listItem.getElem()) {
            String label = Optional.ofNullable(listElem)
                    .map(ListElem::getLabel)
                    .map(LabelElem::getValue)
                    .orElse("");
            String cost = Optional.ofNullable(listElem)
                    .map(ListElem::getCost)
                    .map(CostElem::getPriceDetails)
                    .flatMap(pd -> Optional.ofNullable(pd.getCost())
                            .map(c -> c.getValue() +
                                    Optional.ofNullable(pd.getCurrency()).map(ValueProperty::getValue).orElse("€")))
                    .orElse("");
            String noDiscountCost = Optional.ofNullable(listElem)
                    .map(ListElem::getCost)
                    .map(CostElem::getPriceDetails)
                    .map(PriceDetails::getNoDiscountCost)
                    .map(ValueProperty::getValue)
                    .orElse("");

            templatePriceList.add(new TemplatePrice(label, cost, noDiscountCost));
        }
        return templatePriceList;
    }

    private TemplatePeriodPrices getTemplatesPricesForPeriod(String baseUrl, String authData,
                                                            Integer externalId, String period) throws JsonProcessingException {
        Map<String, List<TemplatePrice>> periodPricesMap = new HashMap<>();
        String url = String.format("https://%s/billmgr?authinfo=%s&func=v2.vds.order.param&pricelist=%s&period=%s&out=json",
                baseUrl, authData, externalId, period);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        List<ListItem> listItems = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getList)
                .orElse(new ArrayList<>());

        for (ListItem listItem : listItems) {
            String listItemName = listItem.getName();
            if (listItemName.equals("pricelist_summary") || listItemName.equals("discount_summary") ||
                    listItemName.equals("total_summary")) {
                periodPricesMap.put(listItem.getName(), getTemplatePricesList(listItem));
            }
        }

        return new TemplatePeriodPrices(periodPricesMap);
    }
}
