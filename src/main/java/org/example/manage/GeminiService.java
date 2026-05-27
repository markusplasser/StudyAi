package org.example.manage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GeminiService {


    private final String API_KEY;
    private final String[] modells = {"gemma-4-31b-it","gemini-2.5-flash","gemini-3.1-flash-lite"};
    private static int[] rateLimit = {};
    private final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final HttpClient client = HttpClient.newHttpClient();

    public GeminiService(String apiKey){
        API_KEY = apiKey;
    }

    //for gemini-2.5-flash
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
    public String buildPromt(String inputtxt, int ammountQuestions, int ammountAnswerPosibileties){
        return "Erstelle " + ammountQuestions + " Fragen mit je "+ ammountAnswerPosibileties + " Antworten zu dem nachfolgendem Infotext :" + inputtxt + ". Die Fragen haben Antworten die mit a) b) c) gekennzeichnent sind wobei nur eine richtig. Diese wird mit 'Antwort: ...' angezeigt . Alle Fragen und Antworten auf Detsch.Bitte nur die Fragen und Antworten ohne irgend einem anderen Text!!";
    }

    public String ask(String prompt, int modell) throws Exception {
        String fullUrl = String.format(BASE_URL, modells[1]) + "?key=" + this.API_KEY;
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

        System.out.println(response.body());
        return response.body();
    }
}