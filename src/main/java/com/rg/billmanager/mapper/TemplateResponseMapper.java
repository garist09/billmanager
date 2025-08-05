package com.rg.billmanager.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rg.billmanager.dto.DocumentResponse;
import com.rg.billmanager.dto.ResponseDoc;
import com.rg.billmanager.contracts.responses.ServerInfoResponse;
import com.rg.billmanager.dto.template.addons.AddonMetadata;
import com.rg.billmanager.dto.template.addons.Field;
import com.rg.billmanager.dto.template.addons.FormMetadata;
import com.rg.billmanager.dto.template.addons.Page;
import com.rg.billmanager.enums.ItemStatus;
import com.rg.billmanager.utility.OptionalUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateResponseMapper {
    private static final String ADDON_PATTERN = "^addon_\\d+$";
    private static final String ROOT = "root";
    private static final String WINDOWS = "windows";
    private static final String ADMINISTRATOR = "Administrator";

    private final ObjectMapper objectMapper;

    public List<Field> parseAddons(ResponseEntity<String> response, DocumentResponse documentResponse,
                                   Set<String> addonNames) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        // Only match keys like "addon_12345" — no suffix
        Pattern addonKeyPattern = Pattern.compile(ADDON_PATTERN);

        Map<String, String> addonMap = getAddonValuesMap(jsonNode, addonKeyPattern);

        JsonNode messagesNode = jsonNode.path("doc").path("messages").path("msg");
        addonMap = addonMap.entrySet().stream()
                .filter(entry -> addonNames.contains(messagesNode.path(entry.getKey()).asText()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        return getAddonsByNames(response, documentResponse, addonMap, addonKeyPattern);
    }

    public ServerInfoResponse mapDocumentResponseToServerInfoResponse(DocumentResponse documentResponse,
                                                                      List<Field> addons) {
        return Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(doc -> {
                    String operationSystem = OptionalUtility.safeGet(doc.getOstempl());
                    String login = ROOT;
                    if (operationSystem.toLowerCase().contains(WINDOWS)) {
                        login = ADMINISTRATOR;
                    }

                    String statusId = documentResponse.getDoc().getStatus().getValue();
                    String status = ItemStatus.getStatusNameById(statusId);

                    return ServerInfoResponse.builder()
                            .id(OptionalUtility.safeGet(doc.getId()))
                            .remoteId(OptionalUtility.safeGet(doc.getRemoteId()))
                            .login(login)
                            .password(OptionalUtility.safeGet(doc.getPassword()))
                            .ip(OptionalUtility.safeGet(doc.getIp()))
                            .hostname(OptionalUtility.safeGet(doc.getDomain()))
                            .creationDate(OptionalUtility.safeGet(doc.getCreationDate()))
                            .expirationDate(OptionalUtility.safeGet(doc.getExpirationDate()))
                            .addons(addons)
                            .reboot(OptionalUtility.safeGet(doc.getReboot()))
                            .status(status)
                            .statusId(statusId)
                            .build();
                }).orElse(null);
    }

    private Map<String, String> getAddonValuesMap(JsonNode jsonNode, Pattern addonKeyPattern) {
        JsonNode docNode = jsonNode.get("doc");

        Map<String, String> addonMap = new HashMap<>();

        if (docNode != null && docNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = docNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();

                if (addonKeyPattern.matcher(key).matches()) {
                    String value = Optional.ofNullable(field.getValue().get("$"))
                            .map(JsonNode::asText)
                            .orElse("");
                    addonMap.put(key, value);
                }
            }
        }

        return addonMap;
    }

    private List<Field> getAddonsByNames(ResponseEntity<String> response, DocumentResponse documentResponse,
                                         Map<String, String> addonMap, Pattern addonKeyPattern)
            throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        List<Page> metadataPages = Optional.ofNullable(documentResponse)
                .map(DocumentResponse::getDoc)
                .map(ResponseDoc::getMetadata)
                .map(AddonMetadata::getForm)
                .map(FormMetadata::getPages)
                .orElse(new ArrayList<>());

        return metadataPages.stream()
                .filter(metadataPage -> metadataPage.getName().equals("addon"))
                .map(Page::getFields)
                .flatMap(fields -> fields.stream())
                .filter(field -> addonKeyPattern.matcher(field.getName()).matches() && addonMap.containsKey(field.getName()))
                .peek(field -> {
                    JsonNode messagesNode = jsonNode.path("doc").path("messages").path("msg");
                    String addonName = messagesNode.path(field.getName()).toString();
                    field.setAddonName(addonName);
                    field.setAddonHintName(messagesNode.path("hint_" + field.getName()).toString());
                    field.setAddonValue(addonMap.get(field.getName()));
                })
                .toList();
    }
}
