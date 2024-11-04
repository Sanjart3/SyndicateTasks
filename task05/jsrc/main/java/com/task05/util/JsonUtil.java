package com.task05.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class JsonUtil {
    public static JSONObject convertJsonToJsonObject(String jsonString) {
        return new JSONObject(jsonString.replace("content", "body"));
    }

    public static String convertObjectToJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
