package com.task02.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import java.util.HashMap;
import java.util.Map;

public class HandlerManager {
    public Map<String, Object> handleRequest(APIGatewayV2HTTPEvent event){
        Handler handler;
        if (getMethod(event).equals("GET")&&getPath(event).equals("/hello")) {
            handler = new HelloHandler();
        } else {
            handler = new ErrorHandler();
        }
        return handler.handle(event);
    }

    public String getMethod(APIGatewayV2HTTPEvent event){
        return event.getRequestContext().getHttp().getMethod();
    }

    public String getPath(APIGatewayV2HTTPEvent event){
        return event.getRequestContext().getHttp().getPath();
    }
}
