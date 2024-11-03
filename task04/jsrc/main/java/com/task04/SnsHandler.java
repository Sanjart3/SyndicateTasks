package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sns_handler",
	roleName = "sns_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(
		targetTopic = "lambda_topic"
)
public class SnsHandler implements RequestHandler<SNSEvent, Map<String, Object>> {

	public Map<String, Object> handleRequest(SNSEvent snsEvent, Context context) {
		Map<String, Object> resultMap = new HashMap();
		snsEvent.getRecords().forEach(record -> {
			resultMap.put(record.getSNS().getMessageId(), record.getSNS().getMessage());
			System.out.println(record.getSNS().getMessage());
		});
		return resultMap;
	}
}
