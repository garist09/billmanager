package com.rg.billmanager.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorUtility {
    private static final Logger logger = LoggerFactory.getLogger(ErrorUtility.class);

    private final ObjectMapper objectMapper;

    public String parseJsonErrorMessage(String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);

        if (root.has("doc") && root.get("doc").has("error")) {
            String error = root.get("doc").get("error").get("msg").get("$").asText() + ". Object: " +
                    root.get("doc").get("error").get("$object");
            logger.error(error);
            return error;
        }

        return "";
    }
}
