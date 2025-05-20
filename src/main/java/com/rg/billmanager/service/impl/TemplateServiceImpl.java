package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.contracts.requests.ServerInfoRequest;
import com.rg.billmanager.contracts.requests.TemplatePlanRequest;
import com.rg.billmanager.dto.template.AppTemplate;
import com.rg.billmanager.dto.DocumentResponse;
import com.rg.billmanager.dto.ResponseDoc;
import com.rg.billmanager.contracts.requests.UpdateServerInfoRequest;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.contracts.responses.TemplatePlansResponse;
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
import com.rg.billmanager.enums.RequestType;
import com.rg.billmanager.exception_handler.exception.InvalidCreationException;
import com.rg.billmanager.mapper.TemplateResponseMapper;
import com.rg.billmanager.service.TemplateService;
import com.rg.billmanager.utility.ErrorUtility;
import com.rg.billmanager.utility.UrlUtils;
import com.rg.billmanager.utility.requestbuilder.ParamBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.UrlBuilderRegistry;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestParamBuilder;
import com.rg.billmanager.utility.requestbuilder.interfaces.RequestUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private static final String IPV4_ADDON_NAME = "Публичные IPv4-адреса";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ErrorUtility errorUtility;
    private final TemplateResponseMapper templateResponseMapper;
    private final ParamBuilderRegistry paramBuilderRegistry;
    private final UrlBuilderRegistry urlBuilderRegistry;

    public TemplatePlansResponse getTemplatesForPlans(String baseUrl, String authData, Integer externalId)
            throws JsonProcessingException {
        RequestUrlBuilder<TemplatePlanRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.TEMPLATE_PLAN);
        String monthPeriodNumber = "1";
        TemplatePlanRequest templatePlanRequest = new TemplatePlanRequest(baseUrl, authData, externalId,
               monthPeriodNumber);
        String url = urlBuilder.buildUrl(templatePlanRequest);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        Map<String, List<OsTemplate>> groupedTemplates = getGroupedTemplates(documentResponse);

        List<Field> fieldList = getAddons(response, documentResponse);

        Map<String, TemplatePeriodPrices> templatePeriodPricesMap = getAllTemplatesPrices(urlBuilder, templatePlanRequest);

        String autoprolong = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(doc -> doc.getAutoprolong().getValue())
                .orElse("");

        return new TemplatePlansResponse(groupedTemplates, fieldList, templatePeriodPricesMap, autoprolong);
    }

    @Override
    public void updateServerInfo(UpdateServerInfoRequest updateServerInfoRequest) throws JsonProcessingException {
        RequestUrlBuilder<UpdateServerInfoRequest> urlBuilder = urlBuilderRegistry
                .getUrlBuilder(RequestType.UPDATE_SERVER_INFO);
        RequestParamBuilder<UpdateServerInfoRequest> paramBuilder = paramBuilderRegistry
                .getParamBuilder(RequestType.UPDATE_SERVER_INFO);

        String url = urlBuilder.buildUrl(updateServerInfoRequest);
        Map<String, String> params = paramBuilder.buildParams(updateServerInfoRequest);

        HttpEntity<String> request = UrlUtils.buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidCreationException(error);
        }
    }

    @Override
    public ServerInfoResponse getServerInfo(String baseUrl, String authData, Integer orderId)
            throws JsonProcessingException {
        RequestUrlBuilder<ServerInfoRequest> urlBuilder = urlBuilderRegistry.getUrlBuilder(RequestType.SERVER_INFO);
        String url = urlBuilder.buildUrl(new ServerInfoRequest(baseUrl, authData, orderId));

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        List<Field> addons = templateResponseMapper.parseAddons(response, documentResponse, Set.of(IPV4_ADDON_NAME));

        return templateResponseMapper.mapDocumentResponseToServerInfoResponse(documentResponse, addons);
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

    private Map<String, TemplatePeriodPrices> getAllTemplatesPrices(RequestUrlBuilder<TemplatePlanRequest> urlBuilder,
                                                                    TemplatePlanRequest templatePlanRequest)
            throws JsonProcessingException {
        List<String> billingPeriods = List.of(BillingPeriod.DAILY.getCode(), BillingPeriod.MONTHLY.getCode(),
                BillingPeriod.QUARTERLY.getCode(), BillingPeriod.SEMI_ANNUAL.getCode(), BillingPeriod.ANNUAL.getCode());
        Map<String, TemplatePeriodPrices> templatePricesByPeriods = new HashMap<>();

        for (String billingPeriod : billingPeriods) {
            templatePlanRequest.setPeriod(billingPeriod);
            templatePricesByPeriods.put(billingPeriod, getTemplatesPricesForPeriod(urlBuilder, templatePlanRequest));
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

    private TemplatePeriodPrices getTemplatesPricesForPeriod(RequestUrlBuilder<TemplatePlanRequest> urlBuilder,
                                                             TemplatePlanRequest templatePlanRequest)
            throws JsonProcessingException {
        Map<String, List<TemplatePrice>> periodPricesMap = new HashMap<>();
        String url = urlBuilder.buildUrl(templatePlanRequest);

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
