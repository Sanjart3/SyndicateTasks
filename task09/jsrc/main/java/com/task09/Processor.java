package com.task09;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.open_meteo.OpenMeteoAPI;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.*;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.Map;

@LambdaHandler(
		lambdaName = "processor",
		roleName = "processor-role",
		isPublishVersion = true,
		aliasName = "learn",
		runtime = DeploymentRuntime.JAVA17,
		layers = {"api-layer"},
		tracingMode = TracingMode.Active,
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
@EnvironmentVariables({
		@EnvironmentVariable(key = "target_table", value = "${target_table}"),
		@EnvironmentVariable(key = "region", value = "${region}")
})
@DependsOn(
		name = "Weather",
		resourceType = ResourceType.DYNAMODB_TABLE
)
@LambdaLayer(
		layerName = "api-layer",
		libraries = {"lib/open-meteo-1.0.1.jar"},
		runtime = DeploymentRuntime.JAVA17,
		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)
public class Processor implements RequestHandler<Object, String> {

	private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
			.withRegion(System.getenv("region"))
			.build();
	private final String table = System.getenv("target_table");

	public String handleRequest(Object request, Context context) {
		OpenMeteoAPI api = new OpenMeteoAPI();
		try {
			String weatherForecast = api.getWeatherForecast();
			Map<String, AttributeValue> weatherEntry = JsonUtil.convertFromJsonToMap(weatherForecast);

			System.out.println("Weather infos: "+weatherEntry);

			dynamoDB.putItem(new PutItemRequest()
					.withTableName(table)
					.withItem(weatherEntry)
			);

			return weatherEntry.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}
}


