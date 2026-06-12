package org.example.GUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class LoadingPopup { //generated
    private final Stage popupStage;

    public LoadingPopup() {
        this.popupStage = new Stage();
        this.popupStage.initStyle(StageStyle.UNDECORATED);

        // Macht das Fenster "Always on Top" (immer im Vordergrund)
        this.popupStage.setAlwaysOnTop(true);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        progressIndicator.setStyle("-fx-progress-color: #0078D7;");

        Label label = new Label("In progress...");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20, 40, 20, 40));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-border-width: 1;");
        root.getChildren().addAll(progressIndicator, label);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.popupStage.setScene(scene);
    }

    public void show() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::show);
            return;
        }

        // TRICK: Holt sich automatisch das aktuell geöffnete Hauptfenster
        Window activeWindow = Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);

        if (activeWindow instanceof Stage) {
            Stage ownerStage = (Stage) activeWindow;
            this.popupStage.initOwner(ownerStage);
            this.popupStage.initModality(Modality.APPLICATION_MODAL); // Sperrt das Hauptfenster!

            // Zentriert das Popup exakt über dem Hauptfenster
            this.popupStage.setOnShown(event -> {
                this.popupStage.setX(ownerStage.getX() + (ownerStage.getWidth() - this.popupStage.getWidth()) / 2);
                this.popupStage.setY(ownerStage.getY() + (ownerStage.getHeight() - this.popupStage.getHeight()) / 2);
            });
        } else {
            // Falls kein Hauptfenster gefunden wurde, einfach auf dem Bildschirm zentrieren
            this.popupStage.centerOnScreen();
        }

        this.popupStage.show();
    }

    public void close() {
        if (Platform.isFxApplicationThread()) {
            this.popupStage.close();
        } else {
            Platform.runLater(popupStage::close);
        }
    }
}