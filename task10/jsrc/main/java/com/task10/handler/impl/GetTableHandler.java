package com.task10.handler.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task10.handler.Handler;
import org.json.JSONArray;
import org.json.JSONObject;


public class GetTableHandler implements Handler {
    private final String tables_table_name = System.getenv("tables_table");
    private final AmazonDynamoDB client;

    public GetTableHandler() {
        this.client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(System.getenv("region"))
                .build();
    }
    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        Table table = getTable();
        JSONArray tablesArray = new JSONArray();

        table.scan(new ScanSpec()).forEach(item -> {
            JSONObject tableObj = getTableObj(item);
            tablesArray.put(tableObj);
        });

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(new JSONObject().put("tables", tablesArray).toString());
    }

    private Table getTable() {
        DynamoDB dynamoDB = new DynamoDB(client);
        return dynamoDB.getTable(tables_table_name);
    }

    private static JSONObject getTableObj(Item item) {
        JSONObject tableObj = new JSONObject();

        if (item.isPresent("minOrder")) {
            tableObj.put("minOrder", item.getInt("minOrder"));
        }

        tableObj.put("id", item.getInt("id"));
        tableObj.put("number", item.getInt("number"));
        tableObj.put("places", item.getInt("places"));
        tableObj.put("isVip", item.getBoolean("isVip"));

        return tableObj;
    }
}
