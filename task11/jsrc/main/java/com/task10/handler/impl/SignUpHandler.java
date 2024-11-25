package com.task10.handler.impl;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.handler.CognitoSupport;
import com.task10.handler.Handler;
import com.task10.model.SignUp;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

public class SignUpHandler extends CognitoSupport implements Handler {

    public SignUpHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        try {
            SignUp signUp = SignUp.fromJson(requestEvent.getBody());

            // sign up
            String userId = cognitoSignUp(signUp)
                    .user().attributes().stream()
                    .filter(attr -> attr.name().equals("sub"))
                    .map(AttributeType::value)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Sub not found."));
            // confirm sign up
            String idToken = confirmSignUp(signUp)
                    .authenticationResult()
                    .idToken();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("message", "User has been successfully signed up.")
                            .put("userId", userId)
                            .put("accessToken", idToken)
                            .toString());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
