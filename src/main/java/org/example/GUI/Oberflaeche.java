package org.example.GUI;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

public class Oberflaeche extends Stage {
    /**
     * Todo!
     * TextField für anzAntworten Pro Frage - darf nur Zahlen erlauben!
     * Umändern das das save File nicht ausgewählt wird sondern nur "FragenFürPhysik" und man keinen Ordner auswählen muss!
     * Abfragen von Fragen!!
     *
     *
     */

    final private Controller controller;

    final MenuItem menuCloseMI, itemerstellen, itemabfragen;

    public Button submit, fragenStarten, antwort1, antwort2, antwort3;
    public TextField anzTF, fragenDateiTF, fragenInhalt;
    public TextArea inputTextTA;
    public BorderPane root;
    public VBox fragenErstellenVB, fragenAbfragenVB, fragenVB;
    public ListView<String> fileLV;

    public int width = 1000;
    public int height = 600;

    public static String savePath;

    public Oberflaeche(Properties p) {

        controller = new Controller(this, p);

        root = new BorderPane();

        /*
         * Fragen erstellen
         */
        fragenErstellenVB = new VBox(15);
        fragenErstellenVB.setPadding(new Insets(30));
        fragenErstellenVB.setStyle("-fx-background-color: #f4f4f4;");

        Label titelL = new Label("Fragen erstellen");
        titelL.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label anzL = new Label("Anzahl der Fragen");
        anzL.setStyle("-fx-font-size: 16px;");

        anzTF = new TextField();
        anzTF.setPromptText("1-15");
        anzTF.setStyle("-fx-font-size: 16px;");
        anzTF.setPrefHeight(40);
        //Lässt nur Zahlen zu;
        anzTF.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        Label inputTextL = new Label("Input Text");
        inputTextL.setStyle("-fx-font-size: 16px;");

        inputTextTA = new TextArea();
        inputTextTA.setPromptText("Kopiere hier deinen Text hinein...");
        inputTextTA.setWrapText(true);
        inputTextTA.setStyle("-fx-font-size: 15px;");

        Label speicherOrtL = new Label("File Name");
        speicherOrtL.setStyle("-fx-font-size: 16px;");

        TextField speicherOrtTF = new TextField();
        speicherOrtTF.setPromptText("Gib einen name für das File ein...");
        speicherOrtTF.setStyle("-fx-font-size: 14px;");
        speicherOrtTF.setPrefHeight(40);

        HBox speicherOrtHB = new HBox(10);
        speicherOrtHB.getChildren().addAll(speicherOrtTF);

        submit = new Button("Fragen erstellen");
        submit.setPrefHeight(45);
        submit.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-background-color: #222222;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
        );
        submit.setOnAction(controller::handle);

        fragenErstellenVB.getChildren().addAll(
                titelL,
                anzL,
                anzTF,
                inputTextL,
                inputTextTA,
                speicherOrtL,
                speicherOrtHB,
                submit
        );

        /*
         * Fragen abfragen
         */
        fragenAbfragenVB = new VBox(15);
        fragenAbfragenVB.setPadding(new Insets(30));
        fragenAbfragenVB.setStyle("-fx-background-color: #f4f4f4;");

        Label abfragenTitelL = new Label("Fragen abfragen");
        abfragenTitelL.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label fragenDateiL = new Label("Fragen-Datei auswählen");
        fragenDateiL.setStyle("-fx-font-size: 16px;");

        fragenDateiTF = new TextField();
        fragenDateiTF.setPromptText("Wähle eine Fragen-Datei aus...");
        fragenDateiTF.setStyle("-fx-font-size: 14px;");
        fragenDateiTF.setPrefHeight(40);

        HBox fragenDateiHB = new HBox(10);
        fragenDateiHB.getChildren().addAll(fragenDateiTF);

        fileLV = new ListView<>();
        fileLV.setPrefHeight(200);
        fileLV.setStyle("-fx-font-size: 14px;");

        fileLV.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                String fileName = newValue;

                if (fileName.endsWith(".txt")) {
                    fileName = fileName.substring(0, fileName.length() - 4);
                }

                fragenDateiTF.setText(fileName);
            }
        });

        fragenStarten = new Button("Fragen starten");
        fragenStarten.setPrefHeight(45);
        fragenStarten.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-background-color: #222222;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
        );

        fragenAbfragenVB.getChildren().addAll(
                abfragenTitelL,
                fragenDateiL,
                fragenDateiHB,
                fileLV,
                fragenStarten
        );

        fragenStarten.setOnAction(controller::handle);
        /**
         * Fragen mit Antwortmöglichkeiten
         */
        fragenVB = new VBox(15);
        fragenVB.setPadding(new Insets(30));
        fragenVB.setStyle("-fx-background-color: #f4f4f4;");
        Label frageNummer = new Label("Frage 1");
        fragenInhalt = new TextField();


        fragenVB.getChildren().addAll(
                frageNummer
        );
        /*
         * Menu erstellen
         */
         itemerstellen = new MenuItem("Fragen erstellen");
         itemabfragen = new MenuItem("Fragen abfragen");

        itemerstellen.setOnAction(controller::handle);
        itemabfragen.setOnAction(controller::handle);
        itemerstellen.setOnAction(controller::handle);

        Menu navigation = new Menu("Navigation");
        navigation.getItems().setAll(itemerstellen, itemabfragen);

        menuCloseMI = new MenuItem("_Close");
        menuCloseMI.addEventHandler(ActionEvent.ACTION, controller);
        menuCloseMI.setMnemonicParsing(true);

        MenuBar menubar = new MenuBar();
        menubar.getMenus().setAll(navigation);

        root.setTop(menubar);
        root.setCenter(fragenErstellenVB);

        Scene scene = new Scene(root, width, height);

        /*
         * Größen prozentuell zur Fenstergröße
         */
        anzTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));

        inputTextTA.prefWidthProperty().bind(scene.widthProperty().multiply(0.75));
        inputTextTA.prefHeightProperty().bind(scene.heightProperty().multiply(0.40));

        speicherOrtTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.55));

        fragenDateiTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.55));

        submit.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));
        fragenStarten.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));

        setScene(scene);
        show();
    }

    public void updateFileList(String fileNamesText) {
        fileLV.getItems().clear();

        if (fileNamesText == null || fileNamesText.isEmpty()) {
            return;
        }

        String[] fileNames = fileNamesText.split(";");

        for (String fileName : fileNames) {
            if (!fileName.isEmpty()) {
                fileLV.getItems().add(fileName);
            }
        }
    }
}