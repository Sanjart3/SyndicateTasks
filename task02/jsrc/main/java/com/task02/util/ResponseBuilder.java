package com.task02.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
    public static Map<String, Object> build(int status, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("statusCode", status);
        result.put("message", message);
        result.put("body", status+"\n"+message);
        return result;
    }
}
