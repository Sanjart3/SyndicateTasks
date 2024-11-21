package com.task09;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.open_meteo.OpenMeteoAPI;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.*;

import java.util.Map;
import java.util.stream.Collectors;

@LambdaHandler(
    lambdaName = "processor",
	roleName = "processor-role",
	isPublishVersion = true,
	aliasName = "learn",
	runtime = DeploymentRuntime.JAVA17,
	layers = {"api-layer"},
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables({
		@EnvironmentVariable(key = "target_table", value = "{target_table}"),
		@EnvironmentVariable(key = "region", value = "{region}")
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

	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard()
			.withRegion(System.getenv("region"))
			.build());
	private final Table table = dynamoDB.getTable(System.getenv("target_table"));

	public String handleRequest(Object request, Context context) {
		OpenMeteoAPI api = new OpenMeteoAPI();
		try {
			String weatherForecast = api.getWeatherForecast();
			Map<String, AttributeValue> weatherEntry = JsonUtil.convertFromJsonToMap(weatherForecast);
			table.putItem(Item.fromMap(convertToSimpleMap(weatherEntry)));
			return weatherEntry.toString();
		} catch (Exception e){
			return e.getMessage();
		}
	}

	private Map<String, Object> convertToSimpleMap(Map<String, AttributeValue> attributeValueMap) {
		return attributeValueMap.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> {
							AttributeValue value = entry.getValue();
							if (value.getS() != null) return value.getS();
							if (value.getN() != null) return Double.parseDouble(value.getN());
							if (value.getL() != null) return value.getL().stream().map(AttributeValue::getS).collect(Collectors.toList());
							if (value.getM() != null) return convertToSimpleMap(value.getM());
							return null; // Handle unsupported types
						}
				));
	}
}
