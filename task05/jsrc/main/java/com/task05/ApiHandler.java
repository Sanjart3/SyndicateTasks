package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.RetentionSetting;
import com.task05.entity.Event;
import com.task05.entity.ResponseBody;
import com.task05.service.EventService;
import com.task05.service.impl.EventServiceImpl;
import com.task05.util.JsonUtil;
import org.json.JSONObject;



@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	architecture = Architecture.X86_64,
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "table", value = "Events"),
		@EnvironmentVariable(key = "region", value = "${region}"),
})
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final EventService eventService;

	public ApiHandler() {
		this.eventService = new EventServiceImpl(System.getenv("region"), System.getenv("table"));
	}

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		JSONObject requestBody = JsonUtil.convertJsonToJsonObject(request.getBody());

		Event event = eventService.putEvent(
				requestBody.getInt("principalId"),
				requestBody.getJSONObject("body").toMap()
		);
		return getApiGatewayProxyResponseEvent(event);
	}

	private static APIGatewayProxyResponseEvent getApiGatewayProxyResponseEvent(Event event) {
		APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
		ResponseBody responseBody = new ResponseBody(201, event);
		apiResponse.setStatusCode(201);
		apiResponse.setBody(JsonUtil.convertObjectToJson(responseBody));
		return apiResponse;
	}
}
