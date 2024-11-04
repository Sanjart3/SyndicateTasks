package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@LambdaHandler(
    lambdaName = "uuid_generator",
	roleName = "uuid_generator-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEventSource(targetRule="uuid_trigger")
@EnvironmentVariable(key = "region", value = "${region}")
public class UuidGenerator implements RequestHandler<Object, String> {

	private static final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
			.withRegion(System.getenv("region")).build();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String S3_BUCKET = "uuid-storage";

	public String handleRequest(Object request, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Generating UUID");
		try
		{
			String fileName = String.valueOf(getNow());
			String fileData = objectMapper.writeValueAsString(generateFileData());

			putObject(fileData, fileName);
			logger.log( "File written and uploaded to S3" );
		}
		catch(Exception ex)
		{
			logger.log("Error : "+ ex.getMessage());
			throw new RuntimeException(ex);
		}
		return "success";
	}

	private static void putObject(String fileData, String fileName) {
		byte[]  contentAsBytes = fileData.getBytes(StandardCharsets.UTF_8) ;
		ByteArrayInputStream contentsAsStream      = new ByteArrayInputStream(contentAsBytes);
		ObjectMetadata md = new ObjectMetadata();
		md.setContentLength(contentAsBytes.length);
		md.setContentType("application/json");
		amazonS3.putObject(new PutObjectRequest(S3_BUCKET, fileName, contentsAsStream, md));
	}

	LocalDate getNow(){
		return LocalDate.now();
	}

	private Map<String, List<String>> generateFileData(){
		Map<String, List<String>> fileData = new HashMap<>();
		fileData.put("ids", generateUuids(10));

		return fileData;
	}

	List<String> generateUuids(int count){
		List<String> uuids = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			uuids.add(UUID.randomUUID().toString());
		}

		return uuids;
	}
}
