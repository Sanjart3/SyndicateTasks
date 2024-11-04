package com.task05.service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.task05.entity.Event;
import com.task05.util.TimeUtil;
import com.task05.service.EventService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventServiceImpl implements EventService {

    private String table;
    private AmazonDynamoDB dynamoDB;
    public EventServiceImpl(String region, String table) {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .build();
        this.table = table;
    }

    @Override
    public Event putEvent(int principalId, Map<String, Object> body) {
        Event event = new Event(
                UUID.randomUUID().toString(),
                principalId,
                TimeUtil.now(),
                body
        );

        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        event.getBody().forEach((key, value) ->
                itemValues.put(key, new AttributeValue().withS(value.toString())));

        dynamoDB.putItem(
                new PutItemRequest()
                        .withTableName(table)
                        .withItem(Map.of(
                                        "id", new AttributeValue().withS(event.getId()),
                                        "principalId", new AttributeValue().withN(Integer.toString(event.getPrincipalId())),
                                        "createdAt", new AttributeValue().withS(event.getCreatedAt()),
                                        "body", new AttributeValue().withM(itemValues)
                                )
                        )
        );
        return event;
    }
}
