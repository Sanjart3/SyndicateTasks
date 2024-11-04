package com.task06;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.task06.service.AuditService;
import com.task06.service.impl.AuditServiceeImpl;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "audit_producer",
	roleName = "audit_producer-role",
	runtime = DeploymentRuntime.JAVA17,
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "region", value = "${region}"),
		@EnvironmentVariable(key = "table", value = "cmtr-2cd95cf2-Audit-test")
})
public class AuditProducer implements RequestHandler<DynamodbEvent, Map<String, Object>> {

	private final AuditService auditService;

	public AuditProducer() {
		this.auditService = new AuditServiceeImpl(
				System.getenv("region"),
				System.getenv("table")
		);
	}

	public Map<String, Object> handleRequest(DynamodbEvent request, Context context) {
		request.getRecords().forEach(dynamodbStreamRecord -> {
			switch (dynamodbStreamRecord.getEventName()) {
				case "INSERT" -> auditService.putAddEvent(dynamodbStreamRecord);
				case "MODIFY" -> auditService.putUpdateEvent(dynamodbStreamRecord);
				default -> throw new IllegalStateException("Unexpected value: " + dynamodbStreamRecord.getEventName());
			}
		});
		return null;
	}
}
