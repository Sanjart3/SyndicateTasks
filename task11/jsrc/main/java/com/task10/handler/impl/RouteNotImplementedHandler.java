package com.task10.handler.impl;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.handler.Handler;
import org.json.JSONObject;

public class RouteNotImplementedHandler implements Handler {

    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(501)
                .withBody(
                        new JSONObject().put(
                                "message",
                                "Handler for the %s method on the %s path is not implemented."
                                        .formatted(requestEvent.getHttpMethod(), requestEvent.getPath())
                        ).toString()
                );
    }
}