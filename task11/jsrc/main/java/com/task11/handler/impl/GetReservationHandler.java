package com.task11.handler.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task11.handler.Handler;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetReservationHandler implements Handler {
    private final String table_name = System.getenv("reservations_table");
    private final AmazonDynamoDB client;

    public GetReservationHandler() {
        this.client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(System.getenv("region"))
                .build();
    }

    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        Table table = getTable();
        JSONArray reservations = new JSONArray();

        table.scan(new ScanSpec()).forEach(item -> {
            JSONObject reservationItem = getreservationItem(item);
            reservations.put(reservationItem);
        });

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(new JSONObject().put("reservations", reservations).toString());
    }

    private static JSONObject getreservationItem(Item item) {
        JSONObject reservationItem = new JSONObject();

        reservationItem.put("id", item.getString("id"));
        reservationItem.put("tableNumber", item.getInt("tableNumber"));
        reservationItem.put("clientName", item.getString("clientName"));
        reservationItem.put("phoneNumber", item.getString("phoneNumber"));
        reservationItem.put("date", item.getString("date"));
        reservationItem.put("slotTimeStart", item.getString("slotTimeStart"));
        reservationItem.put("slotTimeEnd", item.getString("slotTimeEnd"));

        return reservationItem;
    }

    private Table getTable() {
        DynamoDB dynamoDB = new DynamoDB(client);
        return dynamoDB.getTable(table_name);
    }
}