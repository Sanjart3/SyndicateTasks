package com.task09;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, AttributeValue> convertFromJsonToMap(String json) throws Exception {
        JsonNode root = mapper.readTree(json);

        Map<String, AttributeValue> weatherEntry = new HashMap<>();
        weatherEntry.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));

        Map<String, AttributeValue> forecast = createForecastMap(root);
        weatherEntry.put("forecast", new AttributeValue().withM(forecast));

        return weatherEntry;
    }

    private static Map<String, AttributeValue> createForecastMap(JsonNode root) {
        Map<String, AttributeValue> forecast = new HashMap<>();

        forecast.put("elevation", createNumberAttribute(root, "elevation"));
        forecast.put("generationtime_ms", createNumberAttribute(root, "generationtime_ms"));
        forecast.put("hourly", new AttributeValue().withM(createHourlyMap(root.path("hourly"))));
        forecast.put("hourly_units", new AttributeValue().withM(createHourlyUnitsMap(root.path("hourly_units"))));
        forecast.put("latitude", createNumberAttribute(root, "latitude"));
        forecast.put("longitude", createNumberAttribute(root, "longitude"));
        forecast.put("timezone", createStringAttribute(root, "timezone"));
        forecast.put("timezone_abbreviation", createStringAttribute(root, "timezone_abbreviation"));
        forecast.put("utc_offset_seconds", createNumberAttribute(root, "utc_offset_seconds"));

        return forecast;
    }

    private static Map<String, AttributeValue> createHourlyMap(JsonNode hourlyNode) {
        Map<String, AttributeValue> hourly = new HashMap<>();

        hourly.put("temperature_2m", createListAttribute(hourlyNode, "temperature_2m", true));
        hourly.put("time", createListAttribute(hourlyNode, "time", false));

        return hourly;
    }

    private static Map<String, AttributeValue> createHourlyUnitsMap(JsonNode hourlyUnitsNode) {
        Map<String, AttributeValue> hourlyUnits = new HashMap<>();

        hourlyUnits.put("temperature_2m", createStringAttribute(hourlyUnitsNode, "temperature_2m"));
        hourlyUnits.put("time", createStringAttribute(hourlyUnitsNode, "time"));

        return hourlyUnits;
    }

    private static AttributeValue createNumberAttribute(JsonNode node, String fieldName) {
        return new AttributeValue().withN(String.valueOf(node.path(fieldName).asDouble()));
    }

    private static AttributeValue createStringAttribute(JsonNode node, String fieldName) {
        return new AttributeValue().withS(node.path(fieldName).asText());
    }

    private static AttributeValue createListAttribute(JsonNode node, String fieldName, boolean isNumber) {
        return new AttributeValue().withL(
                StreamSupport.stream(node.path(fieldName).spliterator(), false)
                        .map(subNode -> isNumber ? new AttributeValue().withN(String.valueOf(subNode.asDouble())) : new AttributeValue().withS(subNode.asText()))
                        .collect(Collectors.toList())
        );
    }
}
