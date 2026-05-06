package GUI;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Oberflaeche extends Stage {

    final private Controller controller;

    final MenuItem menuCloseMI;

    public Button submit, antwort1, antwort2, antwort3;
    public TextField quelltxt, anz;

    public int width = 1000;
    public int height = 600;

    public Oberflaeche() {
        controller = new Controller(this);

        BorderPane root = new BorderPane();

        /*
         * Fragen erstellen
         */
        VBox fragenErstellenVB = new VBox(15);
        fragenErstellenVB.setPadding(new Insets(30));
        fragenErstellenVB.setStyle("-fx-background-color: #f4f4f4;");

        Label titelL = new Label("Fragen erstellen");
        titelL.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label anzL = new Label("Anzahl der Fragen");
        anzL.setStyle("-fx-font-size: 16px;");

        TextField anzTF = new TextField();
        anzTF.setPromptText("1-15");
        anzTF.setStyle("-fx-font-size: 16px;");
        anzTF.setPrefHeight(40);

        Label inputTextL = new Label("Input Text");
        inputTextL.setStyle("-fx-font-size: 16px;");

        TextArea inputTextTA = new TextArea();
        inputTextTA.setPromptText("Kopiere hier deinen Text hinein...");
        inputTextTA.setWrapText(true);
        inputTextTA.setStyle("-fx-font-size: 15px;");

        Label speicherOrtL = new Label("Speicherort");
        speicherOrtL.setStyle("-fx-font-size: 16px;");

        TextField speicherOrtTF = new TextField();
        speicherOrtTF.setPromptText("Wähle einen Speicherort aus...");
        speicherOrtTF.setEditable(false);
        speicherOrtTF.setStyle("-fx-font-size: 14px;");
        speicherOrtTF.setPrefHeight(40);

        Button speicherOrtBTN = new Button("Ordner wählen");
        speicherOrtBTN.setPrefHeight(40);
        speicherOrtBTN.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: #444444;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
        );

        HBox speicherOrtHB = new HBox(10);
        speicherOrtHB.getChildren().addAll(speicherOrtTF, speicherOrtBTN);

        speicherOrtBTN.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Speicherort auswählen");

            File selectedDirectory = directoryChooser.showDialog(this);

            if (selectedDirectory != null) {
                speicherOrtTF.setText(selectedDirectory.getAbsolutePath());
            }
        });

        Button submitBTN = new Button("Fragen erstellen");
        submitBTN.setPrefHeight(45);
        submitBTN.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-background-color: #222222;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
        );

        fragenErstellenVB.getChildren().addAll(
                titelL,
                anzL,
                anzTF,
                inputTextL,
                inputTextTA,
                speicherOrtL,
                speicherOrtHB,
                submitBTN
        );

        /*
         * Fragen abfragen
         */
        VBox fragenAbfragenVB = new VBox(15);
        fragenAbfragenVB.setPadding(new Insets(30));
        fragenAbfragenVB.setStyle("-fx-background-color: #f4f4f4;");

        Label abfragenTitelL = new Label("Fragen abfragen");
        abfragenTitelL.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label fragenDateiL = new Label("Fragen-Datei auswählen");
        fragenDateiL.setStyle("-fx-font-size: 16px;");

        TextField fragenDateiTF = new TextField();
        fragenDateiTF.setPromptText("Wähle eine Fragen-Datei aus...");
        fragenDateiTF.setEditable(false);
        fragenDateiTF.setStyle("-fx-font-size: 14px;");
        fragenDateiTF.setPrefHeight(40);

        Button fragenDateiBTN = new Button("Datei wählen");
        fragenDateiBTN.setPrefHeight(40);
        fragenDateiBTN.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: #444444;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
        );

        HBox fragenDateiHB = new HBox(10);
        fragenDateiHB.getChildren().addAll(fragenDateiTF, fragenDateiBTN);

        fragenDateiBTN.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Fragen-Datei auswählen");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Textdateien", "*.txt"),
                    new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
            );

            File selectedFile = fileChooser.showOpenDialog(this);

            if (selectedFile != null) {
                fragenDateiTF.setText(selectedFile.getAbsolutePath());
            }
        });

        Button fragenStartenBTN = new Button("Fragen starten");
        fragenStartenBTN.setPrefHeight(45);
        fragenStartenBTN.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-background-color: #222222;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
        );

        fragenAbfragenVB.getChildren().addAll(
                abfragenTitelL,
                fragenDateiL,
                fragenDateiHB,
                fragenStartenBTN
        );

        /*
         * Menu erstellen
         */
        MenuItem itemerstellen = new MenuItem("Fragen erstellen");
        MenuItem itemabfragen = new MenuItem("Fragen abfragen");

        itemerstellen.setOnAction(e -> root.setCenter(fragenErstellenVB));
        itemabfragen.setOnAction(e -> root.setCenter(fragenAbfragenVB));

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
        speicherOrtBTN.prefWidthProperty().bind(scene.widthProperty().multiply(0.18));

        fragenDateiTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.55));
        fragenDateiBTN.prefWidthProperty().bind(scene.widthProperty().multiply(0.18));

        submitBTN.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));
        fragenStartenBTN.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));

        setScene(scene);
        show();
    }
}