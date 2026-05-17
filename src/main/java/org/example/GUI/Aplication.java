package org.example.GUI;

import javafx.application.Application;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.example.manage.AppConfig;

import java.util.Optional;

public class Aplication extends Application {

    private AppConfig appConfig = new AppConfig();

    @Override
    public void init() throws Exception {
        appConfig.loadOrInitialize();

    }
    @Override
    public void start(Stage stage) throws Exception {
        if(appConfig.firstStart()) {
            showApiKeyDialog();
        }
        new Oberflaeche(appConfig.getProperties());
    }
    private boolean showApiKeyDialog() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ersteinrichtung");
        dialog.setHeaderText("Willkommen! Bitte gib deinen Google AI Studio API-Key ein.");
        dialog.setContentText("API-Key:");

        dialog.getDialogPane().setExpanded(true);
        Optional<String> result = dialog.showAndWait();


        if (result.isPresent() && !result.get().trim().isEmpty()) {
            appConfig.saveApiKey(result.get().trim());
            return true;
        }

        return false;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
