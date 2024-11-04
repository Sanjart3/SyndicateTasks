package com.task06.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.task06.service.AuditService;
import com.task06.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuditServiceeImpl implements AuditService {

    private String tableName;
    private AmazonDynamoDB dynamoDB;
    public AuditServiceeImpl(String region, String tableName) {
        this.dynamoDB = AmazonDynamoDBAsyncClientBuilder.standard()
                .withRegion(region).build();
        this.tableName = tableName;
    }

    private void putItem(Map<String, AttributeValue> auditEntry) {
        dynamoDB.putItem(new PutItemRequest().withTableName(tableName).withItem(auditEntry));
    }

    private  Map<String, AttributeValue> getCommonAttributes(DynamodbEvent.DynamodbStreamRecord record){
        Map<String, AttributeValue> auditEvent = new HashMap<>();
        String key = record.getDynamodb().getKeys().get("key").getS();
        auditEvent.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
        auditEvent.put("itemKey", new AttributeValue().withS(key));
        auditEvent.put("modificationTime", new AttributeValue().withS(TimeUtil.now()));

        return auditEvent;
    }

    private static Map<String, AttributeValue> getValueMap(DynamodbEvent.DynamodbStreamRecord record) {
        Map<String, AttributeValue> newValueMap = new HashMap<>();
        newValueMap.put("key", new AttributeValue().withS(record.getDynamodb().getKeys().get("key").getS()));
        newValueMap.put("value", new AttributeValue().withN(record.getDynamodb().getNewImage().get("value").getN()));
        return newValueMap;
    }

    @Override
    public void putAddEvent(DynamodbEvent.DynamodbStreamRecord record) {
        Map<String, AttributeValue> auditEvent = getCommonAttributes(record);
        auditEvent.put("newValue", new AttributeValue().withM(getValueMap(record)));
        putItem(auditEvent);
    }

    @Override
    public void putUpdateEvent(DynamodbEvent.DynamodbStreamRecord record) {
        Map<String, AttributeValue> auditEvent = getCommonAttributes(record);
        putModificationEvent(record, auditEvent);
        putItem(auditEvent);
    }

    private static void putModificationEvent(DynamodbEvent.DynamodbStreamRecord record, Map<String, AttributeValue> auditEvent) {
        auditEvent.put("oldValue", new AttributeValue().withN(record.getDynamodb().getOldImage().get("value").getN()));
        auditEvent.put("newValue", new AttributeValue().withN(record.getDynamodb().getNewImage().get("value").getN()));
        auditEvent.put("updatedAttribute", new AttributeValue().withS("value"));
    }
}
