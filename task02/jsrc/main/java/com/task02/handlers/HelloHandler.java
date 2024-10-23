package com.task02.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.task02.util.ResponseBuilder;

import java.util.Map;

public class HelloHandler implements Handler{
    @Override
    public Map<String, Object> handle(APIGatewayV2HTTPEvent event) {
        int status = 200;
        String message = "{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}";
        return ResponseBuilder.build(200, "Hello from Lambda");
    }
}
