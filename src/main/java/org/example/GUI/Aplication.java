package org.example.GUI;

import javafx.application.Application;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.manage.AppConfig;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class Aplication extends Application {

    private AppConfig appConfig = new AppConfig();

    @Override
    public void init() {
        appConfig.loadOrInitialize();

    }
    @Override
    public void start(Stage stage) {
        if(appConfig.firstStart()) {
            while(!showApiKeyDialog()){
                if(isValidApiKey(appConfig.getProperties().getProperty("API_KEY"))) {
                    break;
                }
            }

        }
        new Oberflaeche(appConfig.getProperties());
    }
    private boolean showApiKeyDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ersteinrichtung");
        dialog.setContentText("API-Key:");

        // 1. Elemente für den Header erstellen
        Text text = new Text("Willkommen! Bitte gib deinen Google AI Studio API-Key ein.");
        Hyperlink link = new Hyperlink("Hier klicken, um zu aistudio.google.com zu gelangen");

        // 2. Klick-Aktion für den Link hinzufügen (öffnet den Standard-Browser)
        link.setOnAction(e -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("https://aistudio.google.com/api-keys"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 3. Text und Link in ein vertikales Layout packen
        VBox headerLayout = new VBox(5); // 5 Pixel Abstand zwischen Text und Link
        headerLayout.setStyle("-fx-padding: 10;"); // Etwas Abstand zu den Rändern
        headerLayout.getChildren().addAll(text, link);

        // 4. Das Layout als Header in die DialogPane setzen
        dialog.getDialogPane().setHeader(headerLayout);
        dialog.getDialogPane().setExpanded(true);

        // Dialog anzeigen und auf Eingabe warten
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            appConfig.saveApiKey(result.get().trim());
        }

        return false;
    }
    public static void main(String[] args) {
        launch(args);
    }

    public static boolean isValidApiKey(String apiKey) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());


            return response.statusCode() == 200;

        } catch (Exception e) {
            return false;
        }
    }
}
