package com.rg.billmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.dto.template.AppTemplate;
import com.rg.billmanager.dto.DocumentResponse;
import com.rg.billmanager.dto.ResponseDoc;
import com.rg.billmanager.contracts.requests.ServerInfoRequest;
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
import com.rg.billmanager.enums.OutFormat;
import com.rg.billmanager.exception_handler.exception.InvalidOrderException;
import com.rg.billmanager.mapper.TemplateResponseMapper;
import com.rg.billmanager.service.TemplateService;
import com.rg.billmanager.utility.ErrorUtility;
import com.rg.billmanager.utility.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rg.billmanager.constants.JsonFields.PERIOD;
import static com.rg.billmanager.constants.UrlConstants.AUTH_INFO;
import static com.rg.billmanager.constants.UrlConstants.BILLMGR;
import static com.rg.billmanager.constants.UrlConstants.DOMAIN;
import static com.rg.billmanager.constants.UrlConstants.ELID;
import static com.rg.billmanager.constants.UrlConstants.FUNC;
import static com.rg.billmanager.constants.UrlConstants.HTTPS;
import static com.rg.billmanager.constants.UrlConstants.OUT;
import static com.rg.billmanager.constants.UrlConstants.PRICELIST;
import static com.rg.billmanager.constants.UrlConstants.REBOOT;
import static com.rg.billmanager.constants.UrlConstants.SOK;
import static com.rg.billmanager.enums.FunctionName.ORDER_PARAM;
import static com.rg.billmanager.enums.FunctionName.VDS_EDIT;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private static final String IPV4_ADDON_NAME = "Публичные IPv4-адреса";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ErrorUtility errorUtility;
    private final TemplateResponseMapper templateResponseMapper;

    public TemplatePlansResponse getTemplatesForPlans(String baseUrl, String authData, Integer externalId)
            throws JsonProcessingException {
        String monthPeriodNumber = "1";
        String url = getUrlTemplatePrices(baseUrl, authData, externalId, monthPeriodNumber);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        DocumentResponse documentResponse = objectMapper.readValue(response.getBody(), DocumentResponse.class);

        Map<String, List<OsTemplate>> groupedTemplates = getGroupedTemplates(documentResponse);

        List<Field> fieldList = getAddons(response, documentResponse);

        Map<String, TemplatePeriodPrices> templatePeriodPricesMap = getAllTemplatesPrices(baseUrl, authData, externalId);

        String autoprolong = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(doc -> doc.getAutoprolong().getValue())
                .orElse("");

        return new TemplatePlansResponse(groupedTemplates, fieldList, templatePeriodPricesMap, autoprolong);
    }

    @Override
    public void updateServerInfo(ServerInfoRequest serverInfoRequest) throws JsonProcessingException {
        String url = UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(serverInfoRequest.getBaseUrl())
                .path(BILLMGR)
                .build().toString();

        Map<String, String> params = getServerInfoRequestParams(serverInfoRequest);
        HttpEntity<String> request = UrlUtils.buildFormUrlEncodedEntity(params);
        String response = restTemplate.postForObject(url, request, String.class);

        String error = errorUtility.parseJsonErrorMessage(response);

        if (!error.isEmpty()) {
            throw new InvalidOrderException(error);
        }
    }

    @Override
    public ServerInfoResponse getServerInfo(String baseUrl, String authData, Integer orderId)
            throws JsonProcessingException {
        String url = getServerInfoUrl(baseUrl, authData, orderId);

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
        String url = getUrlTemplatePrices(baseUrl, authData, externalId, period);

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

    private String getUrlTemplatePrices(String baseUrl, String authData, Integer externalId, String period) {
        return UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(baseUrl)
                .path(BILLMGR)
                .queryParam(AUTH_INFO, authData)
                .queryParam(FUNC, ORDER_PARAM.getName())
                .queryParam(PRICELIST, externalId)
                .queryParam(PERIOD, period)
                .queryParam(OUT, OutFormat.JSON.getName())
                .build().toString();
    }

    private Map<String, String> getServerInfoRequestParams(ServerInfoRequest serverInfoRequest) {
        Map<String, String> params = new HashMap<>();
        params.put(AUTH_INFO, serverInfoRequest.getAuthData());
        params.put(ELID, serverInfoRequest.getId());
        params.put(DOMAIN, serverInfoRequest.getHostname());
        params.put(REBOOT, serverInfoRequest.getReboot());
        params.put(FUNC, VDS_EDIT.getName());
        serverInfoRequest.getAddons().entrySet().forEach(entry -> params.put(entry.getKey(), entry.getValue()));
        params.put(SOK, "ok");
        params.put(OUT, OutFormat.XJSON.getName());
        return params;
    }

    private String getServerInfoUrl(String baseUrl, String authData, Integer orderId) {
        return UriComponentsBuilder.newInstance()
                .scheme(HTTPS)
                .host(baseUrl)
                .path(BILLMGR)
                .queryParam(AUTH_INFO, authData)
                .queryParam(FUNC, VDS_EDIT.getName())
                .queryParam(ELID, orderId)
                .queryParam(OUT, OutFormat.XJSON.getName())
                .build().toString();
    }
}
