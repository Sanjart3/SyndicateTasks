package com.task10.handler.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.handler.Handler;
import org.json.JSONObject;

public class GetTableByIdHandler implements Handler {
    private final String tables_table_name = System.getenv("tables_table");
    private final AmazonDynamoDB client;

    public GetTableByIdHandler() {
        this.client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(System.getenv("region"))
                .build();
    }

    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        String tableId = extractTableId(requestEvent);

        try {
            Table table = getTable();
            Item item = table.getItem("id", Integer.parseInt(tableId));

            if (item == null) {
                return buildErrorResponse(404, "Table not found!");
            }

            JSONObject tableObject = createTableJson(item);
            return buildSuccessResponse(tableObject);

        } catch (NumberFormatException e) {
            return buildErrorResponse(400, "Invalid table ID format.");
        }
    }

    private Table getTable() {
        DynamoDB dynamoDB = new DynamoDB(client);
        return dynamoDB.getTable(tables_table_name);
    }

    private String extractTableId(APIGatewayProxyRequestEvent request) {
        return request.getPath().replace("/tables/", "");
    }

    private JSONObject createTableJson(Item item) {
        JSONObject tableObject = new JSONObject();
        tableObject.put("id", item.getInt("id"));
        tableObject.put("number", item.getInt("number"));
        tableObject.put("places", item.getInt("places"));
        tableObject.put("isVip", item.getBoolean("isVip"));

        if (item.isPresent("minOrder")) {
            tableObject.put("minOrder", item.getInt("minOrder"));
        }

        return tableObject;
    }

    private APIGatewayProxyResponseEvent buildSuccessResponse(JSONObject tableObject) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(tableObject.toString());
    }

    private APIGatewayProxyResponseEvent buildErrorResponse(int statusCode, String errorMessage) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(new JSONObject().put("error", errorMessage).toString());
    }
}