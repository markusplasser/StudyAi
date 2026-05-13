package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GeminiService {


    private final String API_KEY;
    private final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemma-4-31b-it:generateContent";

    private final HttpClient client = HttpClient.newHttpClient();

    public GeminiService(String apiKey){
        API_KEY = apiKey;
    }

    public String getString(String responseBody){
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(responseBody, JsonObject.class);
        System.out.println(responseBody);
        return json.getAsJsonArray("candidates").
                get(0).getAsJsonObject().
                getAsJsonObject("content").
                getAsJsonArray("parts").
                get(0).getAsJsonObject().
                get("text").getAsString();
    }

    public String ask(String prompt) throws Exception {
        String fullUrl = BASE_URL + "?key=" + this.API_KEY;
        String body = """
            {
              "contents": [{
                "parts": [{"text": "%s"}]
              }]
            }
            """.formatted(prompt.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Response parsen (z.B. mit Jackson oder Gson)
        // response.body() enthält das JSON
        return response.body();
    }
}