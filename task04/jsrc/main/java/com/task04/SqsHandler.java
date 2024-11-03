package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.*;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(
		targetQueue = "async_queue",
		batchSize = 10
)
public class SqsHandler implements RequestHandler<SQSEvent, Map<String, Object>> {

	public Map<String, Object> handleRequest(SQSEvent sqsEvents, Context context) {
		System.out.println("Hello from lambda");
		Map<String, Object> resultMap = new HashMap<>();
		sqsEvents.getRecords().stream()
						.forEach(record -> {
							resultMap.put(record.getMessageId(), record.getBody());
							System.out.println(record.getBody());
						});
		return resultMap;
	}
}