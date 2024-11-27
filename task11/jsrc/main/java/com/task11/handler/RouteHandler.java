package com.task11.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task11.handler.impl.*;
import com.task11.model.RouteKey;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.HashMap;
import java.util.Map;

public class  RouteHandler {
    private final Map<RouteKey, Handler> handlers = new HashMap<>();

    public RouteHandler(CognitoIdentityProviderClient cognitoProvider) {

        handlers.put(new RouteKey( "/signup", "POST"), new SignUpHandler(cognitoProvider));
        handlers.put(new RouteKey( "/signin", "POST"), new SignInHandler(cognitoProvider));
        handlers.put(new RouteKey( "/tables","POST"), new PostTableHandler());
        handlers.put(new RouteKey( "/tables", "GET"), new GetTableHandler());
        handlers.put(new RouteKey("/tables/{tableId}" ,"GET"), new GetTableByIdHandler());
        handlers.put(new RouteKey( "/reservations", "POST"), new PostReservationHandler());
        handlers.put(new RouteKey( "/reservations", "GET"), new GetReservationHandler());
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent) {
        RouteKey routeKey = getRouteKey(requestEvent);
        Handler handler = handlers.getOrDefault(routeKey, new RouteNotImplementedHandler());
        return handler.handle(requestEvent);
    }

    private RouteKey getRouteKey(APIGatewayProxyRequestEvent requestEvent) {
        String path = filterTableId(requestEvent);
        return new RouteKey(path, requestEvent.getHttpMethod());
    }

    private static String filterTableId(APIGatewayProxyRequestEvent requestEvent) {
        String path = requestEvent.getPath();
        if (path.matches("/tables/\\d+")) {
            path = "/tables/{tableId}";
        }
        return path;
    }

    private String getMethod(APIGatewayProxyRequestEvent requestEvent) {
        return requestEvent.getHttpMethod();
    }

    private String getPath(APIGatewayProxyRequestEvent requestEvent) {
        return requestEvent.getPath();
    }
}
