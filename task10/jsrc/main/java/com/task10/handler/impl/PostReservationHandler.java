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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostReservationHandler implements Handler {
    private final String reservations_table_name = System.getenv("reservations_table");
    private final String tables_table_name = System.getenv("tables_table");
    private final AmazonDynamoDB client;

    public PostReservationHandler() {
        this.client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(System.getenv("region"))
                .build();
    }

    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent requestEvent) {
        JSONObject requestBody = new JSONObject(requestEvent.getBody());

        int tableNumber = requestBody.getInt("tableNumber");
        validateTableNumber(tableNumber);
        checkTableAvailability(tableNumber);

        String reservationId = createReservation(requestBody, tableNumber);

        return buildSuccessResponse(reservationId);
    }

    private void validateTableNumber(int tableNumber) {
        List<Integer> availableTables = getAvailableTables();
        if (!availableTables.contains(tableNumber)) {
            throw new IllegalArgumentException("Table not found");
        }
    }

    private List<Integer> getAvailableTables() {
        DynamoDB dynamoDB = new DynamoDB(client);
        Table tablesTable = dynamoDB.getTable(tables_table_name);
        List<Integer> tables = new ArrayList<>();
        tablesTable.scan(new ScanSpec()).forEach(item -> {
            tables.add(item.getInt("number"));
        });
        return tables;
    }

    private void checkTableAvailability(int tableNumber) {
        DynamoDB dynamoDB = new DynamoDB(client);
        Table reservationTable = dynamoDB.getTable(reservations_table_name);
        reservationTable.scan(new ScanSpec()).forEach(item -> {
            if (item.getInt("tableNumber") == tableNumber) {
                throw new IllegalArgumentException("Table is already reserved for this date!");
            }
        });
    }

    private String createReservation(JSONObject requestBody, int tableNumber) {
        String reservationId = UUID.randomUUID().toString();
        Item item = new Item()
                .withPrimaryKey("id", reservationId)
                .withInt("tableNumber", tableNumber)
                .withString("clientName", requestBody.getString("clientName"))
                .withString("phoneNumber", requestBody.getString("phoneNumber"))
                .withString("date", requestBody.getString("date"))
                .withString("slotTimeStart", requestBody.getString("slotTimeStart"))
                .withString("slotTimeEnd", requestBody.getString("slotTimeEnd"));

        DynamoDB dynamoDB = new DynamoDB(client);
        Table reservationTable = dynamoDB.getTable(reservations_table_name);
        reservationTable.putItem(item);

        return reservationId;
    }

    private APIGatewayProxyResponseEvent buildSuccessResponse(String reservationId) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(new JSONObject().put("reservationId", reservationId).toString());
    }

}