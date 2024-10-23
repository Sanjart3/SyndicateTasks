package com.task02.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseBuilder {
    public static Map<String, Object> build(int status, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("statusCode", status);
        result.put("message", message);
        return result;
    }
}
