package com.task10.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public interface Handler {
    APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent);
}
