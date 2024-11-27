package com.task11.handler.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task11.handler.Handler;
import org.json.JSONObject;

public class PostTableHandler implements Handler {
    private final String tables_table_name = System.getenv("tables_table");
    private final AmazonDynamoDB client;

    public PostTableHandler() {
        this.client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(System.getenv("region"))
                .build();
    }

    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        JSONObject requestBody = new JSONObject(requestEvent.getBody());

        Table table = getTable();

        Item item = createItemFromRequest(requestBody);
        table.putItem(item);

        return buildSuccessResponse(requestBody.getInt("id"));
    }

    private Table getTable() {
        DynamoDB dynamoDB = new DynamoDB(client);
        return dynamoDB.getTable(tables_table_name);
    }

    private Item createItemFromRequest(JSONObject requestBody) {
        Item item = new Item()
                .withPrimaryKey("id", requestBody.getInt("id"))
                .withInt("number", requestBody.getInt("number"))
                .withInt("places", requestBody.getInt("places"))
                .withBoolean("isVip", requestBody.getBoolean("isVip"));

        if (requestBody.has("minOrder")) {
            item = item.withInt("minOrder", requestBody.getInt("minOrder"));
        }

        return item;
    }

    private APIGatewayProxyResponseEvent buildSuccessResponse(int id) {
        JSONObject responseBody = new JSONObject().put("id", id);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(responseBody.toString());
    }
}