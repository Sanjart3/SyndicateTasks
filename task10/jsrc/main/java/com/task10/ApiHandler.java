package com.task10;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.task10.handler.RouteHandler;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.Map;

import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_CLIENT_ID;
import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_USER_POOL_ID;


@LambdaHandler(lambdaName = "api_handler",
		roleName = "api_handler-role",
		runtime = DeploymentRuntime.JAVA17,
		aliasName = "${lambdas_alias_name}",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "region", value = "${region}"),
		@EnvironmentVariable(key = "tables_table", value = "${tables_table}"),
		@EnvironmentVariable(key = "reservations_table", value = "${reservations_table}"),
		@EnvironmentVariable(key = "booking_userpool", value = "${booking_userpool}"),
		@EnvironmentVariable(key = "COGNITO_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_USER_POOL_ID),
		@EnvironmentVariable(key = "CLIENT_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_CLIENT_ID)
})
@DependsOn(
		resourceType = ResourceType.COGNITO_USER_POOL,
		name = "${booking_userpool}"
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final RouteHandler routeHandler;

	public ApiHandler() {
		CognitoIdentityProviderClient cognitoClient = initCognitoClient();
		routeHandler = new RouteHandler(cognitoClient);
	}

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		try {
			return routeHandler
					.handleRequest(request)
					.withHeaders(initHeadersForCORS());
		} catch (Exception e) {
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(400)
					.withBody("Error in process: " + e.getMessage());
		}
	}

	private CognitoIdentityProviderClient initCognitoClient() {
		return CognitoIdentityProviderClient.builder()
				.region(Region.of(System.getenv("region")))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
	}

	private Map<String, String> initHeadersForCORS() {
		return Map.of(
				"Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
				"Access-Control-Allow-Origin", "*",
				"Access-Control-Allow-Methods", "*",
				"Accept-Version", "*"
		);
	}
}