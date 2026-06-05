package org.example.manage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

public class GeminiService {


    private final String API_KEY;

    private final String[] modells = {"gemma-4-31b-it", "gemini-2.5-flash", "gemini-3.1-flash-lite"};
    private static int[] rateLimit = {};
    private final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final HttpClient client = HttpClient.newHttpClient();

    public GeminiService(String apiKey) {
        API_KEY = apiKey;
    }

    public String getString(int model, String responseBody) {
        switch (model) {
            case 0:
                return getStringGemma(responseBody);
            case 1:
                return getStringGeminiFlash(responseBody);
            case 2:
                return getGeminiLite(responseBody);
            default:
                return null;
        }
    }

    public String getGeminiLite(String responseBody){
        return null;
    }

    //for gemini-2.5-flash
    public String getStringGeminiFlash(String responseBody) {
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

    //gemma-4-31b-it
    public String getStringGemma(String responseBody) {
        JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray parts = root
                .getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts");

        for (JsonElement element : parts) {
            JsonObject part = element.getAsJsonObject();
            boolean isThought = part.has("thought") && part.get("thought").getAsBoolean();
            if (!isThought) {
                return part.get("text").getAsString();
            }
        }
        return null;
    }

    public String buildPromt(String inputtxt, int ammountQuestions, int ammountAnswerPosibileties) {
        return "Generiere " + ammountQuestions + " Multiple-Choice-Fragen (je " + ammountAnswerPosibileties + " Optionen, a/b/c...) basierend auf diesem Text: \"" + inputtxt + "\".\n" +
                "Regeln:\n" +
                "- Sprache: Deutsch\n" +
                "- Genau eine Option ist korrekt\n" +
                "- Formatierung: Direkt nach den Antwortmöglichkeiten folgt 'Antwort: [Richtige Option-Ganze Antwort]'\n" +
                "- Jeder Satz/Zeile muss durch einen Zeilenumbruch getrennt sein\n" +
                "- Ausgabe: NUR Fragen und Antworten, kein Smalltalk, keine Einleitung, kein Markdown-Code-Block.";
    }


    public String ask(String prompt, int modell) {
        String fullUrl = String.format(BASE_URL, modells[modell]) + "?key=" + this.API_KEY;
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

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }

        System.out.println(response.body());
        return response.body();
    }
}