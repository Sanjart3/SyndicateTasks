package com.task08;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenMeteoClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final Double LATITUDE = 52.52;
    private static final Double LONGTITUSE = 13.419998;
    private static final String PARAMS = "hourly=temperature_2m,precipitation";

    public String getWeatherForecast()
            throws IOException, InterruptedException {
        // Build the full URL with query parameters
        String url = String.format("%s?latitude=%.4f&longitude=%.4f&%s", BASE_URL, LATITUDE, LONGTITUSE, PARAMS);

        // Create an HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse and return the response as a JSON object
        return JsonParser.parseString(response.body()).getAsJsonObject().toString();
    }
}