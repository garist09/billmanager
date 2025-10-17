package com.rg.billmanager.utility.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.rg.billmanager.dto.ServerConfig;
import com.rg.billmanager.dto.ServerResources;
import com.rg.billmanager.enums.BillingPeriod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rg.billmanager.constants.JsonFields.CORES_FIELD_NAME;
import static com.rg.billmanager.constants.JsonFields.COST;
import static com.rg.billmanager.constants.JsonFields.CURRENCY;
import static com.rg.billmanager.constants.JsonFields.DESCRIPTION;
import static com.rg.billmanager.constants.JsonFields.DETAIL;
import static com.rg.billmanager.constants.JsonFields.DISK_FIELD_NAME;
import static com.rg.billmanager.constants.JsonFields.NETWORK_SPEED_FIELD_NAME;
import static com.rg.billmanager.constants.JsonFields.PERIOD;
import static com.rg.billmanager.constants.JsonFields.PRICE;
import static com.rg.billmanager.constants.JsonFields.PRICES;
import static com.rg.billmanager.constants.JsonFields.RAM_FIELD_NAME;
import static com.rg.billmanager.constants.JsonFields.TITLE;

@Component
@RequiredArgsConstructor
public class BillManagerParser {
    public static final String RUB = "rub";
    private static final Logger logger = LoggerFactory.getLogger(BillManagerParser.class);
    private static final String VIRTUAL_SERVER_TYPE = "virtual";
    private static final String DDR_4_RAM_TYPE = "DDR4";
    private static final String RAM_DISK = "ram/disk";
    private static final String CORES_NETWORK_SPEED = "cores/network_speed";
    private static final String MiB_RU = "МиБ";
    private static final String MB_RU = "МБ";
    private static final String GiB_RU = "ГиБ";
    private static final String GB_EN = "GB";
    private static final String GB_RU = "ГБ";
    private static final String MBPS_EN = "Mbps";
    private static final String GBPS_EN = "Gbps";
    private static final String COUNT_NAME = "Шт.";
    private static final String MB_EN = "MB";
    private static final String TB_EN = "TB";
    private static final String TB_RU = "ТБ";
    private static final String GBPS_RU = "Гбит";
    private static final String MHz_RU = "МГц";
    private static final String MHz_EN = "MHz";
    private static final String Mb_EN = "Mb";
    private static final String GHZ_EN = "GHz";
    public static final String EURO_SYMBOL = "€";
    public static final String EUR_NAME = "eur";
    public static final String DOLLAR_SYMBOL = "$";
    public static final String DOLLAR_NAME = "usd";
    public static final String RUB_SYMBOL = "₽";
    public static final String RUB_UPPERCASE = "RUB";
    public static final String RUB_SYMBOL_RU = "p.";

    public void parseServerConfigs(JsonNode json, Map<String, String> datacenter, List<ServerConfig> serverConfigList,
                                   Integer serverConfigId) {
        String name = datacenter.get("name");
        JsonNode servers = json.path("doc").path("list").get(0).path("elem");
        for (JsonNode server : servers) {
            Integer externalId = server.path("id").path(DOLLAR_SYMBOL).asInt();
            ServerConfig serverConfig = parseServerConfig(externalId, server, name);
            serverConfig.setId(serverConfigId.toString());
            serverConfigList.add(serverConfig);
        }
    }

    private ServerConfig parseServerConfig(Integer externalId, JsonNode server, String datacenterName) {
        ServerResources serverResources = createServerResources(server);
        String name = getJsonNodeValue(server, TITLE);
        String description = getJsonNodeValue(server, DESCRIPTION);

        extractResourcesFromName(name, serverResources);
        extractResourcesFromDescription(description, serverResources);
        extractProcessorInfo(name, serverResources);
        convertNetworkSpeedGbpsToMbps(serverResources);

        Map<String, String> billingCycleMap = new HashMap<>();
        String currency = EURO_SYMBOL;
        if (server.get(PRICES) != null && server.get(PRICES).get(PRICE) != null &&
                server.get(PRICES).get(PRICE).isArray()) {
            JsonNode pricesNode = server.get(PRICES).get(PRICE);
            currency = getCurrency(pricesNode);
            fillBillingCycle(pricesNode, billingCycleMap);
        }

        return ServerConfig.builder()
                .name(name)
                .description(description)
                .prices(billingCycleMap)
                .currency(currency)
                .serverType(VIRTUAL_SERVER_TYPE)
                .externalId(externalId)
                .location(datacenterName)
                .serverResources(serverResources)
                .build();
    }

    private ServerResources createServerResources(JsonNode server) {
        JsonNode detail = server.has(DETAIL) ? server.get(DETAIL) : NullNode.getInstance();

        String coresField = getDetailValue(detail, CORES_FIELD_NAME);
        Integer cores = cleanAndConvertToMb.apply(coresField, false);
        String ramField = getDetailValue(detail, RAM_FIELD_NAME);
        Integer ram = cleanAndConvertToMb.apply(ramField, true);
        String diskField = getDetailValue(detail, DISK_FIELD_NAME);
        Integer disk = cleanAndConvertToMb.apply(diskField, true);
        String networkSpeedField = getDetailValue(detail, NETWORK_SPEED_FIELD_NAME);
        Integer networkSpeed = cleanAndConvertToMb.apply(networkSpeedField, false);

        return ServerResources.builder()
                .processorName("")
                .ramType(DDR_4_RAM_TYPE)
                .cores(cores)
                .ram(ram)
                .diskType("")
                .disk(disk)
                .coreFrequency("")
                .traffic(0.0)
                .networkSpeed(networkSpeed)
                .build();
    }

    BiFunction<String, Boolean, Integer> cleanAndConvertToMb = (value, isMemory) -> {
        String name = isMemory ? RAM_DISK : CORES_NETWORK_SPEED;

        if (value == null || value.trim().isEmpty()) {
            logger.info("     " + name + ": empty value");
            return 0;
        }

        String valueStr = value.trim();
        logger.info("     " + name + ": initial value '" + valueStr + "'");
        int result = 0;

        if (valueStr.contains(COUNT_NAME)) {
            String cleaned = valueStr.replace(COUNT_NAME, "").trim();
            result = cleaned.isEmpty() ? 0 : (int) Float.parseFloat(cleaned);
            logger.info("     " + name + ": found cores '" + valueStr + "' -> " + result);
        } else if (valueStr.contains(MiB_RU)) {
            String cleaned = valueStr.replace(MiB_RU, "").trim();
            result = cleaned.isEmpty() ? 0 : (int) Float.parseFloat(cleaned);
            logger.info("     " + name + ": found RAM '" + valueStr + "' -> " + result + MB_RU);
        } else if (valueStr.contains(MB_RU)) {
            String cleaned = valueStr.replace(MB_RU, "").trim();
            result = cleaned.isEmpty() ? 0 : (int) Float.parseFloat(cleaned);
            logger.info("     " + name + ": found memory/disk '" + valueStr + "' -> " + result + MB_RU);
        } else if (valueStr.contains(GiB_RU)) {
            String cleaned = valueStr.replace(GiB_RU, "").trim();
            double gbValue = cleaned.isEmpty() ? 0 : Double.parseDouble(cleaned);
            result = (int) (gbValue * 1024);
            logger.info("     " + name + ": found disk '" + valueStr + "' -> " + gbValue + GB_EN + " = " + result + MB_EN);
        } else {
            String cleaned = valueStr.replace(GB_EN, "")
                    .replace(GB_RU, "")
                    .replace(MBPS_EN, "")
                    .replace(GBPS_EN, "")
                    .trim();
            result = cleaned.isEmpty() ? 0 : (int) Float.parseFloat(cleaned);

            if (isMemory && result > 0) {
                result *= 1024; // GB → MB
            }

            logger.info("     " + name + ": '" + valueStr + "' -> " + result);
        }
        return result;
    };

    private String getDetailValue(JsonNode detailArray, String keyName) {
        if (detailArray.isArray()) {
            for (JsonNode item : detailArray) {
                String name = item.path("name").path(DOLLAR_SYMBOL).asText("");
                if (name.equalsIgnoreCase(keyName)) {
                    return item.path("value").path(DOLLAR_SYMBOL).asText("");
                }
            }
        }

        return "";
    }

    private String getJsonNodeValue(JsonNode server, String keyName) {
        String value = "";
        if (server.has(keyName)) {
            JsonNode node = server.get(keyName);
            if (node.has(DOLLAR_SYMBOL)) {
                value = node.get(DOLLAR_SYMBOL).asText();
            }
        }

        return value;
    }

    private void fillBillingCycle(JsonNode pricesNode, Map<String, String> billingCycleMap) {
        for (JsonNode priceNode : pricesNode) {
            JsonNode period = priceNode.get(PERIOD);
            JsonNode cost = priceNode.get(COST);
            String billingCycle = BillingPeriod.getLabelByCode(period.path(DOLLAR_SYMBOL).asText(""));
            String billingCost = cost.path(DOLLAR_SYMBOL).asText("");
            billingCycleMap.put(billingCycle, billingCost);
        }
    }

    private void extractResourcesFromDescription(String description, ServerResources serverResources) {
        logger.info("Extracting resources from description");

        if (description == null || description.isEmpty()) {
            logger.info("Description is empty");
            return;
        }

        // Remove HTML tags
        String cleanDescription = description.replaceAll("<[^>]+>", "");
        logger.info("Cleaned description: " +
                (cleanDescription.length() > 200 ? cleanDescription.substring(0, 200) + "..." : cleanDescription)
        );

        parseCoreResourcesFromDescription(serverResources, cleanDescription);
        parseRamResourcesFromDescription(serverResources, cleanDescription);
        parseDiskResourcesFromDescription(serverResources, cleanDescription);
        parseNetworkSpeedResources(serverResources, cleanDescription);
        parseTrafficResources(serverResources, cleanDescription);
        logger.info("Extracted resources: " + serverResources);
    }

    private void extractResourcesFromName(String name, ServerResources serverResources) {
        logger.info("Extract resource from name: '" + name + "'");

        parseCoreResourcesFromName(serverResources, name);
        parseRamResourcesFromName(serverResources, name);
        parseDiskResourcesFromName(serverResources, name);
        parseNetworkSpeedResources(serverResources, name);
        parseTrafficResources(serverResources, name);
    }

    private void extractProcessorInfo(String name, ServerResources serverResources) {
        logger.info("Extract info about the process from: '" + name + "'");

        parseProcessorResources(serverResources, name);
        parseCoreFrequencyResources(serverResources, name);

        logger.info("Extracted processor information: " + serverResources.getProcessorName() + " " +
                serverResources.getCoreFrequency());
    }

    private void parseCoreResourcesFromDescription(ServerResources serverResources, String cleanDescription) {
        String[] coresPatterns = {
                "\\[(\\d+)\\s*vCore\\]",           // [4 vCore]
                "(\\d+)\\s*vCore",                 // 4 vCore
                "vCore\\s*x?(\\d+)",               // vCore x10
                "(\\d+)\\s*CPU",                   // 4 CPU
                "(\\d+)\\s*ядер",                  // 4 ядер
                "(\\d+)\\s*core",                  // 4 core
                "x(\\d+)",                         // x10
                "Процессор:\\s*(\\d+)\\s*ядр[оа]?", // Процессор: 4 ядра
                "(\\d+)\\s*ядр[оа](?:\\s|,|$)"     // 4 ядра
        };

        parseCoreResources(serverResources, cleanDescription, coresPatterns);
    }

    private void parseCoreResourcesFromName(ServerResources serverResources, String string) {
        String[] coresPatterns = {
                "\\[(\\d+)\\s*vCore\\]",      // [4 vCore]
                "(\\d+)\\s*vCore",            // 4 vCore
                "(\\d+)\\s*(?:v?Core|Physical\\s+Cores?)",
                "(\\d+)\\s*Шт\\.",            // 4 Шт.
                "(\\d+)\\s*(?:CPU|ЦП)",
                "(\\d+)c",                    // 4c
                "x(\\d+)"                     // x10
        };

        parseCoreResources(serverResources, string, coresPatterns);
    }

    private void parseCoreResources(ServerResources serverResources, String string, String[] coresPatterns) {
        for (String pattern : coresPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(string);
            if (matcher.find()) {
                serverResources.setCores(Integer.parseInt(matcher.group(1)));
                logger.info("Found cores: " + serverResources.getCores() + " (pattern: " + pattern + ")");
                break;
            }
        }
    }

    private void parseRamResourcesFromDescription(ServerResources serverResources, String cleanDescription) {
        String[] ramPatterns = {
                "(\\d+)\\s*DDR[3-5]\\s*(?:ECC\\s*REG)?",
                "(\\d+)\\s*DDR[3-5]\\s*\\d+\\s*МГц",
                "(\\d+)\\s*GB\\s*RAM",
                "(\\d+)\\s*Гб\\s*RAM",
                "(\\d+)\\s*ГБ\\s*ОЗУ",
                "(\\d+)\\s*ГиБ",
                "(\\d+)\\s*GB\\s*(?:памяти|memory)",
                "(\\d+)\\s*Гб\\s*(?:памяти|memory)",
                "Память:\\s*(\\d+)\\s*GB",
                "Память:\\s*(\\d+)\\s*ГБ",
                "Память:\\s*(\\d+)\\s*MB",
                "Память:\\s*(\\d+)\\s*МБ",
                "Память:\\s*(\\d+)\\s*Mb"
        };

        for (String pattern : ramPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(cleanDescription);
            if (matcher.find()) {
                int ramValue = Integer.parseInt(matcher.group(1));
                String matchedText = matcher.group(0).toUpperCase();

                if (matchedText.contains(MB_EN) || matchedText.contains(MB_RU) || matcher.group(0).contains(Mb_EN)) {
                    serverResources.setRam(ramValue);
                    logger.info("Found RAM: " + ramValue + MB_EN + " (pattern: " + pattern + ")");
                } else {
                    serverResources.setRam(ramValue * 1024);
                    logger.info("Found RAM: " + ramValue + GB_EN + " = " + serverResources.getRam() + MB_EN +
                            " (pattern: " + pattern + ")");
                }
                break;
            }
        }
    }

    private void parseRamResourcesFromName(ServerResources serverResources, String name) {
        String[] ramPatterns = {
                "(\\d+)\\s*DDR[3-5]\\s*(?:ECC\\s*REG)?",
                "(\\d+)\\s*DDR[3-5]\\s*\\d+\\s*МГц",
                "(\\d+)\\s*GB\\s*RAM",
                "(\\d+)\\s*ГиБ",
                "(\\d+)\\s*(?:GB|ГБ)\\s*(?:DDR|ОЗУ|RAM)",
                "(\\d+)g"
        };

        for (String pattern : ramPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(name);
            if (matcher.find()) {
                int ramGb = Integer.parseInt(matcher.group(1));
                int ramMb = ramGb * 1024;
                serverResources.setRam(ramMb);
                logger.info("Found RAM: " + ramGb + GB_EN + " = " + ramMb + MB_EN + " (pattern: " + pattern + ")");
                break;
            }
        }
    }

    private void parseDiskResourcesFromDescription(ServerResources serverResources, String string) {
        String[] diskPatterns = {
                "(\\d+)\\s*ГБ\\s*(?:SSD|NVME|HDD|SAS)",
                "(\\d+(?:\\.\\d+)?)\\s*ТБ\\s*(?:SSD|NVME|HDD|SAS)",
                "(\\d+)\\s*GB\\s*(?:SSD|NVME|HDD|SAS)",
                "(\\d+)\\s*Гб\\s*(?:SSD|NVME|HDD|SAS)",
                "(\\d+(?:\\.\\d+)?)\\s*TB\\s*(?:SSD|NVME|HDD|SAS)",
                "(\\d+)\\s*GB\\s*(?:диск|disk)",
                "(\\d+)\\s*Гб\\s*(?:диск|disk)",
                "Диск:\\s*(\\d+)\\s*GB\\s*(?:SSD|NVME|HDD|SAS)",
                "Диск:\\s*(\\d+)\\s*ГБ\\s*(?:SSD|NVME|HDD|SAS)",
                "(\\d+)\\s*GB\\s*(?:SSD|NVME|HDD|SAS)(?:\\s|,|$)",
                "(\\d+)\\s*ГБ\\s*(?:SSD|NVME|HDD|SAS)(?:\\s|,|$)"
        };

        parseDiskResources(serverResources, string, diskPatterns);
    }

    private void parseDiskResourcesFromName(ServerResources serverResources, String string) {
        String[] diskPatterns = {
                "(\\d+)\\s*ГБ\\s*(?:SSD|NVME|HDD)",
                "(\\d+(?:\\.\\d+)?)\\s*ТБ\\s*(?:SSD|NVME|HDD)",
                "(\\d+)\\s*GB\\s*(?:SSD|NVMe|HDD)",
                "(\\d+(?:\\.\\d+)?)\\s*TB\\s*(?:SSD|NVMe|HDD)",
                "(\\d+)\\s*ГиБ",
                "(\\d+)\\s*(?:GB|ГБ)\\s*(?:SSD|NVMe|HDD)"
        };

        parseDiskResources(serverResources, string, diskPatterns);
    }

    private void parseDiskResources(ServerResources serverResources, String string, String[] diskPatterns) {
        for (String pattern : diskPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(string);
            if (matcher.find()) {
                double diskValue = Double.parseDouble(matcher.group(1));
                String matchedText = matcher.group(0).toUpperCase();

                // TB -> GB -> MB
                if (matchedText.contains(TB_EN) || matchedText.contains(TB_RU)) {
                    diskValue *= 1024; // TB → GB
                }
                int diskMb = (int) (diskValue * 1024);
                serverResources.setDisk(diskMb); // GB → MB
                logger.info("Found disk: " + diskValue + GB_EN + " = " + serverResources.getDisk() + MB_EN +
                        " (pattern: " + pattern + ")");
                break;
            }
        }
    }

    private void parseNetworkSpeedResources(ServerResources serverResources, String cleanDescription) {
        String[] speedPatterns = {
                "(\\d+)\\s*Mbps",
                "(\\d+)\\s*Gbps",
                "(\\d+)\\s*Мбит",
                "(\\d+)\\s*Гбит",
                "Канал:\\s*(\\d+)Mb/s",
                "Канал:\\s*(\\d+)Mbps",
                "(\\d+)Mb/s"
        };

        for (String pattern : speedPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(cleanDescription);
            if (matcher.find()) {
                int speedValue = Integer.parseInt(matcher.group(1));
                if (matcher.group(0).contains(GBPS_EN) || matcher.group(0).contains(GBPS_RU))
                    speedValue *= 1000; // Gbps → Mbps
                serverResources.setNetworkSpeed(speedValue);
                logger.info("Found network speed: " + serverResources.getNetworkSpeed() + MBPS_EN +
                        " (pattern: " + pattern + ")");
                break;
            }
        }
    }

    private void parseTrafficResources(ServerResources serverResources, String cleanDescription) {
        String[] trafficPatterns = {
                "Трафик:\\s*(\\d+(?:\\.\\d+)?)\\s*Tb",
                "Трафик:\\s*(\\d+(?:\\.\\d+)?)\\s*ТБ",
                "(\\d+(?:\\.\\d+)?)\\s*Tb(?:\\s|,|$)",
                "(\\d+(?:\\.\\d+)?)\\s*ТБ(?:\\s|,|$)",
                "(\\d+(?:\\.\\d+)?)\\s*TB(?:\\s|,|$)"
        };

        for (String pattern : trafficPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(cleanDescription);
            if (matcher.find()) {
                serverResources.setTraffic(Double.parseDouble(matcher.group(1)));
                logger.info("Found traffic: " + serverResources.getTraffic() + TB_EN + " (pattern: " + pattern + ")");
                break;
            }
        }
    }

    private void parseProcessorResources(ServerResources serverResources, String name) {
        String[] processorPatterns = {
                "(AMD\\s+Epyc\\s+\\d+)",            // AMD Epyc 7282
                "(Intel\\s+Xeon\\s+[\\w-]+)",       // Intel Xeon E5-2680
                "(AMD\\s+Ryzen\\s+\\d+\\s+\\d+X?)", // AMD Ryzen 9 7950X
                "(Intel\\s+Core\\s+i\\d+-\\d+)",    // Intel Core i7-9700
                "(Ryzen\\s+\\d+\\s+\\d+X?)",        // Ryzen 9 7950X
                "(Epyc\\s+\\d+)",                   // Epyc 7282
                "(Xeon\\s+[\\w-]+)",                // Xeon E5-2680
                "(\\d*x?E\\d+-\\d+)",               // 2xE5-2680
                "(E\\d+-\\d+)",                     // E5-2680
                "(Core\\s+i\\d+)"                   // Core i7
        };

        for (String pattern : processorPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(name);
            if (matcher.find()) {
                String processorName = matcher.group(1).trim();
                serverResources.setProcessorName(processorName);
                logger.info("Found processor: '" + processorName + "' (pattern: " + pattern + ")");
                break;
            }
        }
    }

    private void parseCoreFrequencyResources(ServerResources serverResources, String name) {
        String[] frequencyPatterns = {
                "(\\d+(?:\\.\\d+)?)\\s*ГГц",
                "(\\d+(?:\\.\\d+)?)\\s*GHz",
                "(\\d+(?:\\.\\d+)?)\\s*МГц",
                "(\\d+(?:\\.\\d+)?)\\s*MHz",
                "\\((\\d+(?:\\.\\d+)?)\\s*ГГц\\)",
                "\\((\\d+(?:\\.\\d+)?)\\s*GHz\\)",
                "\\((\\d+(?:\\.\\d+)?)\\s*МГц\\)",
                "\\((\\d+(?:\\.\\d+)?)\\s*MHz\\)",
                "@\\s*(\\d+(?:\\.\\d+)?)\\s*ГГц",
                "@\\s*(\\d+(?:\\.\\d+)?)\\s*GHz"
        };

        for (String pattern : frequencyPatterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(name);
            if (matcher.find()) {
                double freqValue = Double.parseDouble(matcher.group(1));
                String matchedText = matcher.group(0);
                double freqGHz;

                if (matchedText.contains(MHz_RU) || matchedText.contains(MHz_EN)) {
                    freqGHz = freqValue / 1000;
                    logger.info("Found core frequncy in " + MHz_EN + ": " + freqValue + MHz_EN + " -> " + freqGHz +
                            GHZ_EN + " (паттерн: " + pattern + ")");
                } else {
                    freqGHz = freqValue;
                    logger.info("Found core frequncy in " + GHZ_EN + ": " + freqGHz + GHZ_EN +
                            " (pattern: " + pattern + ")");
                }

                serverResources.setCoreFrequency(String.valueOf(freqGHz));
                break;
            }
        }
    }

    private void convertNetworkSpeedGbpsToMbps(ServerResources serverResources) {
        int networkSpeed = serverResources.getNetworkSpeed();
        if (String.valueOf(networkSpeed).contains(GBPS_EN)) {
            networkSpeed *= 1000;
            serverResources.setNetworkSpeed(networkSpeed);
        }
    }

    private String getCurrency(JsonNode pricesNode) {
        String currency = pricesNode.get(0).get(CURRENCY).get(DOLLAR_SYMBOL).asText(EURO_SYMBOL);
        if (currency.equals(EURO_SYMBOL) || currency.equalsIgnoreCase(EUR_NAME)) {
            currency = EUR_NAME;
        } else if (currency.equals(DOLLAR_SYMBOL) || currency.equalsIgnoreCase(DOLLAR_NAME)) {
            currency = DOLLAR_NAME;
        } else if (currency.equals(RUB_SYMBOL) || currency.equalsIgnoreCase(RUB) ||
                currency.equalsIgnoreCase(RUB_SYMBOL_RU) || currency.equalsIgnoreCase(RUB_UPPERCASE)) {
            currency = RUB;
        }
        return currency;
    }
}
