package com.task02.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import java.util.Map;

public interface Handler {
    Map<String, Object> handle(APIGatewayV2HTTPEvent event);
}
