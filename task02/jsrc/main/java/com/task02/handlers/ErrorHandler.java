package com.task02.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.task02.util.ResponseBuilder;

import java.util.Map;

public class ErrorHandler implements Handler{
    @Override
    public Map<String, Object> handle(APIGatewayV2HTTPEvent event) {
        int status = 400;
        String path = event.getRawPath();
        String method = event.getRequestContext().getHttp().getMethod();
        String message = "{\"statusCode\": 400, \"message\": \"" + String.format( "Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, method) + "\"}";
        return ResponseBuilder.build(status, message);
    }
}
