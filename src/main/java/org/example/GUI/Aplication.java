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

    /**
     * popup window that requires the user to copy a valid API KEY or the window reopens
     * @return always false
     */
    private boolean showApiKeyDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ersteinrichtung");
        dialog.setContentText("API-Key:");

        Text text = new Text("Willkommen! Bitte gib deinen Google AI Studio API-Key ein.");
        Hyperlink link = new Hyperlink("Hier klicken, um zu aistudio.google.com zu gelangen");


        link.setOnAction(e -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("https://aistudio.google.com/api-keys"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        VBox headerLayout = new VBox(5);
        headerLayout.setStyle("-fx-padding: 10;");
        headerLayout.getChildren().addAll(text, link);

        dialog.getDialogPane().setHeader(headerLayout);
        dialog.getDialogPane().setExpanded(true);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            appConfig.saveApiKey(result.get().trim());
        }

        return false;
    }
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * tests if the API KEY is valid
     * @param apiKey API KEY
     * @return true if the KEY is valid
     */
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
