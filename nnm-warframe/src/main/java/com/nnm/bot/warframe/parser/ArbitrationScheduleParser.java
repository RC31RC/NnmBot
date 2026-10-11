package com.nnm.bot.warframe.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnm.bot.warframe.application.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.config.properties.WarframeArbitrationProperties;
import com.nnm.bot.warframe.model.ArbitrationScheduleEntry;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

@Component
public class ArbitrationScheduleParser {
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private WarframeArbitrationProperties properties;
    @Resource
    private WarframeWorldStateCodebookService codebookService;

    public List<ArbitrationScheduleEntry> parse(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Empty arbitration schedule");
        }
        String value = payload.trim();
        // Keep the old JSON shape readable for already downloaded fixtures. The configured
        // production source is arbys.txt and therefore uses the strict text parser below.
        return value.startsWith("[") ? parseJson(value) : parseText(value);
    }

    private List<ArbitrationScheduleEntry> parseText(String text) {
        if (codebookService == null) {
            throw new IllegalStateException("WorldState codebook is required for arbys.txt");
        }
        var rows = new TreeMap<Instant, String>();
        int lineNumber = 0;
        for (String rawLine : text.split("\\R", -1)) {
            lineNumber++;
            String line = rawLine.trim();
            if (line.isBlank() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }
            String[] columns = line.split(",", -1);
            if (columns.length != 2 || columns[0].isBlank() || columns[1].isBlank()) {
                throw new IllegalArgumentException("Invalid arbys.txt line " + lineNumber);
            }
            long epochSeconds;
            try {
                epochSeconds = Long.parseLong(columns[0].trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid arbys.txt timestamp at line "
                        + lineNumber, exception);
            }
            if (epochSeconds <= 0) {
                throw new IllegalArgumentException("Invalid arbys.txt timestamp at line "
                        + lineNumber);
            }
            Instant activation;
            try {
                activation = Instant.ofEpochSecond(epochSeconds);
            } catch (RuntimeException exception) {
                throw new IllegalArgumentException("Invalid arbys.txt timestamp at line "
                        + lineNumber, exception);
            }
            String nodeReference = columns[1].trim();
            if (nodeReference.length() > 200 || rows.containsKey(activation)) {
                throw new IllegalArgumentException(
                        "Duplicate or invalid arbys.txt activation at line " + lineNumber);
            }
            if (rows.size() >= properties.getMaxRecords()) {
                throw new IllegalArgumentException("Arbitration schedule exceeds max records");
            }
            rows.put(activation, nodeReference);
        }
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("arbys.txt contains no schedule rows");
        }

        var entries = rows.entrySet().stream()
                .map(row -> resolveTextEntry(row.getKey(), row.getValue()))
                .toList();
        Instant previousEnd = null;
        for (ArbitrationScheduleEntry entry : entries) {
            if (previousEnd != null && !entry.activation().equals(previousEnd)) {
                throw new IllegalArgumentException("Non-contiguous arbitration schedule at "
                        + entry.activation());
            }
            previousEnd = entry.expiry();
        }
        return entries;
    }

    private ArbitrationScheduleEntry resolveTextEntry(Instant activation, String nodeReference) {
        var node = codebookService.findNode(nodeReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown arbitration node reference: " + nodeReference));
        String nodeName = requiredText(node.displayName(), "node " + nodeReference, 200);
        String missionReference = requiredText(
                node.relatedReference(), "mission type for " + nodeReference, 200);
        var mission = codebookService.findMissionType(missionReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown arbitration mission type reference: " + missionReference));
        String factionReference = requiredText(
                node.factionReference(), "faction for " + nodeReference, 200);
        var faction = codebookService.findFaction(factionReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown arbitration faction reference: " + factionReference));
        String planet = extractPlanet(nodeName);
        if (planet.isBlank()) {
            throw new IllegalArgumentException(
                    "Arbitration node has no planet in codebook display name: " + nodeReference);
        }
        return new ArbitrationScheduleEntry(
                "arbys:" + activation.getEpochSecond(),
                activation,
                activation.plus(Duration.ofHours(1)),
                nodeName,
                requiredText(mission.displayName(), "mission type " + missionReference, 100),
                requiredText(faction.displayName(), "faction " + factionReference, 100),
                planet,
                "{}");
    }

    private List<ArbitrationScheduleEntry> parseJson(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root == null || !root.isArray() || root.isEmpty() || root.size() > properties.getMaxRecords()) {
                throw new IllegalArgumentException("Invalid arbitration schedule array");
            }
            var entries = new TreeMap<Instant, ArbitrationScheduleEntry>();
            for (JsonNode row : root) {
                Instant start = Instant.parse(required(row, "activation", 40));
                Instant end = Instant.parse(required(row, "expiry", 40));
                if (!end.isAfter(start) || !Duration.between(start, end).equals(Duration.ofHours(1))) {
                    throw new IllegalArgumentException("Invalid arbitration interval");
                }
                String node = required(row, "node", 200);
                String planet = row.path("planet").asText("").trim();
                if (planet.isBlank()) {
                    String normalized = node.replace('（', '(').replace('）', ')');
                    int opening = normalized.lastIndexOf('(');
                    if (opening >= 0 && normalized.endsWith(")")) {
                        planet = normalized.substring(opening + 1, normalized.length() - 1).trim();
                    }
                }
                if (planet.length() > 100 || (row.hasNonNull("bounds") && !row.get("bounds").isObject())) {
                    throw new IllegalArgumentException("Invalid arbitration planet or bounds");
                }
                var entry = new ArbitrationScheduleEntry(required(row, "id", 100), start, end, node,
                        required(row, "missionType", 100), required(row, "enemy", 100), planet,
                        row.hasNonNull("bounds") ? row.get("bounds").toString() : "{}");
                var previous = entries.putIfAbsent(start, entry);
                if (previous != null && !previous.equals(entry)) {
                    throw new IllegalArgumentException("Conflicting arbitration entries at " + start);
                }
            }
            Instant previousEnd = null;
            for (var entry : entries.values()) {
                if (previousEnd != null && entry.activation().isBefore(previousEnd)) {
                    throw new IllegalArgumentException("Overlapping arbitration intervals");
                }
                previousEnd = entry.expiry();
            }
            return List.copyOf(entries.values());
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid arbitration JSON", exception);
        }
    }

    private static String extractPlanet(String nodeName) {
        String normalized = nodeName.replace('（', '(').replace('）', ')');
        int opening = normalized.lastIndexOf('(');
        if (opening < 0 || !normalized.endsWith(")")) {
            return "";
        }
        return normalized.substring(opening + 1, normalized.length() - 1).trim();
    }

    private static String requiredText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException("Invalid arbitration field: " + field);
        }
        return value.trim();
    }

    private String required(JsonNode row, String field, int maxLength) {
        JsonNode value = row.path(field);
        if (!value.isTextual() || value.asText().isBlank() || value.asText().length() > maxLength) {
            throw new IllegalArgumentException("Invalid arbitration field: " + field);
        }
        return value.asText().trim();
    }
}
