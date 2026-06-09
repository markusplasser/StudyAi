package org.example.GUI;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.manage.Fragen_Antworten;

import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import java.util.Collections;

public class Oberflaeche extends Stage {

    final private Controller controller;

    final MenuItem menuCloseMI, itemerstellen, itemabfragen;


    public Button submit, fragenStarten, nextQuestion, antwort1, antwort2, antwort3, home, again;
    public TextField anzTF, anzAntwTF, fragenDateiTF, speicherOrtTF;
    public Label frage, quizFertig, richtig;
    public TextArea inputTextTA;
    public BorderPane root;
    public VBox fragenErstellenVB, fragenAbfragenVB, fragenVB, ergebnissVB;
    public ListView<String> fileLV;

    public Button[] awnserButtons;

    public int width  = 1020;
    public int height = 640;
    public int fragenNum = 0;
    public int anzRichtig = 0;
    public int anzFragen = 0;

    public Fragen_Antworten[] fragenArr;
    public Integer[] randomOrder;

    private static final String BG_DEEP     = "#0F1117";
    private static final String BG_SURFACE  = "#1A1D27";
    private static final String BG_CARD     = "#22263A";
    private static final String BG_INPUT    = "#2A2F45";
    private static final String ACCENT      = "#7C6AFA";
    private static final String ACCENT_GLOW = "#9B8DFF";
    private static final String ACCENT_DIM  = "#3D3570";
    private static final String TEXT_HI     = "#F0F0FF";
    private static final String TEXT_MID    = "#9A99B3";
    private static final String BORDER_COL  = "#2E3250";

    private static final String FIELD_STYLE =
            "-fx-background-color: " + BG_INPUT + ";" +
                    "-fx-text-fill: " + TEXT_HI + ";" +
                    "-fx-prompt-text-fill: " + TEXT_MID + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: " + BORDER_COL + ";" +
                    "-fx-border-radius: 8;" +
                    "-fx-padding: 8 12;" +
                    "-fx-font-size: 14px;";

    private static final String BTN_ACCENT =
            "-fx-background-color: " + ACCENT + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 11 24;";

    private static final String BTN_ACCENT_HOVER =
            "-fx-background-color: " + ACCENT_GLOW + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 11 24;";

    public static final String BTN_OUTLINE =
            "-fx-background-color: " + ACCENT_DIM + ";" +
                    "-fx-text-fill: " + ACCENT_GLOW + ";" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: " + ACCENT + ";" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 11 24;";

    public static final String BTN_OUTLINE_HOVER =
            "-fx-background-color: " + ACCENT + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: " + ACCENT_GLOW + ";" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 11 24;";

    private static final String BTN_RED =
            "-fx-background-color: #3d1515;" +
                    "-fx-text-fill: #ff6b6b;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #e03131;" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 11 24;";

    private static final String BTN_GREEN =
            "-fx-background-color: #0f3d1f;" +
                    "-fx-text-fill: #6bff8f;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #2e8b57;" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 11 24;";


    public Oberflaeche(Properties p) {
        controller = new Controller(this, p);

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DEEP + ";");

        buildFragenErstellen();
        buildFragenAbfragen();

        itemerstellen = new MenuItem("Fragen erstellen");
        itemabfragen  = new MenuItem("Fragen abfragen");
        itemerstellen.setOnAction(controller::handle);
        itemabfragen.setOnAction(controller::handle);

        menuCloseMI = new MenuItem("_Close");
        menuCloseMI.addEventHandler(ActionEvent.ACTION, controller);
        menuCloseMI.setMnemonicParsing(true);

        Menu navigation = new Menu("Navigation");
        navigation.getItems().setAll(itemerstellen, itemabfragen);

        MenuBar menubar = new MenuBar();
        menubar.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-border-color: " + BORDER_COL + ";" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-padding: 2 10;"
        );
        menubar.getMenus().setAll(navigation);

        root.setTop(menubar);
        root.setCenter(fragenErstellenVB);

        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.web(BG_DEEP));

        anzTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.28));
        inputTextTA.prefWidthProperty().bind(scene.widthProperty().multiply(0.72));
        inputTextTA.prefHeightProperty().bind(scene.heightProperty().multiply(0.38));
        speicherOrtTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.55));
        fragenDateiTF.prefWidthProperty().bind(scene.widthProperty().multiply(0.38));
        submit.prefWidthProperty().bind(scene.widthProperty().multiply(0.22));
        fragenStarten.prefWidthProperty().bind(scene.widthProperty().multiply(0.22));

        setScene(scene);
        show();
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_MID + ";"
        );
        return l;
    }

    private Label titleLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_HI + ";"
        );
        return l;
    }

    private Separator darkSep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: " + BORDER_COL + "; -fx-opacity: 1;");
        return s;
    }

    private void buildFragenErstellen() {
        anzTF         = styledTextField("1 – 15");
        inputTextTA   = styledTextArea("Kopiere hier deinen Text hinein...");
        anzTF        = styledTextField("1 – 15");
        inputTextTA  = styledTextArea("Kopiere hier deinen Text hinein...");
        speicherOrtTF = styledTextField("Name für die Datei...");
        anzAntwTF     = styledTextField("3-5");
        submit        = accentButton("Fragen erstellen");

        anzTF.setPrefHeight(42);
        speicherOrtTF.setPrefHeight(42);
        anzAntwTF.setPrefHeight(42);
        submit.setOnAction(controller::handle);

        // Nur Zahlen erlauben
        anzTF.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) return change;
            return null;
        }));
        anzAntwTF.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || (newText.matches("[0-9]*") && Integer.parseInt(newText) >= 3 && Integer.parseInt(newText) <= 5)) {
                return change;
            }
            return null;
        }));



        fragenErstellenVB = new VBox(14);
        fragenErstellenVB.setPadding(new Insets(38, 44, 38, 44));
        fragenErstellenVB.setStyle("-fx-background-color: " + BG_DEEP + ";");
        fragenErstellenVB.getChildren().addAll(
                titleLabel("Fragen erstellen"), darkSep(),
                sectionLabel("ANZAHL DER FRAGEN"), anzTF,
                sectionLabel("ANZAHL DER ANTWORTMÖGLICHKEITEN"), anzAntwTF,
                sectionLabel("EINGABE TEXT"), inputTextTA,
                sectionLabel("DATEINAME"), speicherOrtTF,
                submit
        );
    }

    private void buildFragenAbfragen() {
        fragenDateiTF = styledTextField("Wähle eine Datei aus der Liste...");
        fragenDateiTF.setPrefHeight(42);
        fragenDateiTF.setEditable(false);

        fileLV = new ListView<>();
        fileLV.setPrefHeight(220);
        fileLV.setStyle(
                "-fx-background-color: " + BG_CARD + ";" +
                        "-fx-control-inner-background: " + BG_CARD + ";" +
                        "-fx-border-color: " + BORDER_COL + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + TEXT_HI + ";"
        );
        fileLV.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) fragenDateiTF.setText(n);
        });

        fragenStarten = accentButton("Fragen starten");
        fragenStarten.setOnAction(controller::handle);

        GridPane gp = new GridPane();
        gp.setPadding(new Insets(38, 44, 38, 44));
        gp.setHgap(32);
        gp.setVgap(14);
        gp.setStyle("-fx-background-color: " + BG_DEEP + ";");

        gp.add(titleLabel("Fragen abfragen"),     0, 0, 2, 1);
        gp.add(darkSep(),                          0, 1, 2, 1);
        gp.add(sectionLabel("AUSGEWÄHLTE DATEI"), 0, 2);
        gp.add(fragenDateiTF,                      0, 3);
        gp.add(fragenStarten,                      0, 4);
        gp.add(sectionLabel("VERFÜGBARE DATEIEN"),1, 2);
        gp.add(fileLV,                             1, 3, 1, 3);

        fragenAbfragenVB = new VBox();
        fragenAbfragenVB.setStyle("-fx-background-color: " + BG_DEEP + ";");
        fragenAbfragenVB.getChildren().add(gp);
    }

    public void buildFragenView(int anzAntworten) {
        frage = new Label("Frage");
        frage.setWrapText(true);
        frage.setMaxWidth(Double.MAX_VALUE);
        frage.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_HI + ";" +
                        "-fx-background-color: " + BG_CARD + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + ACCENT_DIM + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 18 22;"
        );

        awnserButtons = new Button[anzAntworten];
        for(int i = 0; i < anzAntworten; i++) {
            awnserButtons[i] = outlineButton("Antwort " + (i + 1));
            awnserButtons[i].setOnAction(controller::handle);
        }

        nextQuestion = accentButton("Weiter →");
        nextQuestion.setOnAction(controller::handle);

        fragenVB = new VBox(14);
        fragenVB.setPadding(new Insets(38, 44, 38, 44));
        fragenVB.setStyle("-fx-background-color: " + BG_DEEP + ";");
        fragenVB.setAlignment(Pos.TOP_LEFT);
        fragenVB.getChildren().add(frage);
        for(Button b : awnserButtons) fragenVB.getChildren().add(b);
        fragenVB.getChildren().add(nextQuestion);
    }

    public void buildErgebniss() {
        ergebnissVB = new VBox(20);
        ergebnissVB.setPadding(new Insets(38, 44, 38, 44));
        ergebnissVB.setStyle("-fx-background-color: " + BG_DEEP + ";");
        ergebnissVB.setAlignment(Pos.CENTER);

        Label trophy = new Label("🏆");
        trophy.setStyle(
                "-fx-font-size: 52px;" +
                        "-fx-text-fill: gold;" +
                        "-fx-font-weight: bold;"
        );

        quizFertig = new Label("Quiz abgeschlossen!");
        quizFertig.setStyle(
                "-fx-text-fill: " + TEXT_HI + ";" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;");

        darkSep();

        VBox card = new VBox(14);
        card.setPadding(new Insets(24, 32, 24, 32));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: " + BG_CARD + ";" +
                        "-fx-border-color: " + BORDER_COL + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-width: 1;");

        Label punkte = new Label(anzRichtig + " / " + anzFragen);
        punkte.setStyle(
                "-fx-text-fill: " + ACCENT_GLOW + ";" +
                        "-fx-font-size: 52px;" +
                        "-fx-font-weight: bold;");


        float prozentF = ((float) anzRichtig/anzFragen) * 100;
        int prozentInt = (int) prozentF;
        String badgeColor;
        String badgeBg;
        String badgeBorder;
        String badgeText;

        if (prozentInt >= 70) {
            badgeColor = "#6bff8f";
            badgeBg = "#0f3d1f";
            badgeBorder = "#2e8b57";
            badgeText = "Ausgezeichnet!";
        }
        else if (prozentInt >= 40) {
            badgeColor = "#FFD166";
            badgeBg = "#3d3000";
            badgeBorder = "#b38600";
            badgeText = "Gut gemacht!";
        }
        else {
            badgeColor = "#ff6b6b";
            badgeBg = "#3d1515";
            badgeBorder = "#e03131";
            badgeText = "Weiter üben!";
        }

        Label badge = new Label(prozentInt + "%  " + badgeText);
        badge.setStyle(
                "-fx-background-color: " + badgeBg + ";" +
                        "-fx-text-fill: " + badgeColor + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + badgeBorder + ";" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5 16;");

        richtig = new Label(anzRichtig + " von " + anzFragen + " Fragen richtig beantwortet");
        richtig.setStyle(
                "-fx-text-fill: " + TEXT_MID + ";" +
                        "-fx-font-size: 14px;");

        card.getChildren().addAll(punkte, badge, richtig);

        home = new Button("Zurück zur Übersicht");
        home.setPrefHeight(46);
        home.setStyle(BTN_OUTLINE);
        home.setOnMouseEntered(e -> home.setStyle(BTN_OUTLINE_HOVER));
        home.setOnMouseExited(e  -> home.setStyle(BTN_OUTLINE));
        home.setOnAction(controller::handle);

        again = new Button("Quiz wiederholen");
        again.setPrefHeight(46);
        again.setStyle(BTN_OUTLINE);
        again.setOnMouseEntered(e -> again.setStyle(BTN_OUTLINE_HOVER));
        again.setOnMouseExited(e  -> again.setStyle(BTN_OUTLINE));
        again.setOnAction(controller::handle);

        HBox btnRow = new HBox(14, home, again);
        btnRow.setAlignment(Pos.CENTER);

        ergebnissVB.getChildren().addAll(trophy, quizFertig, darkSep(), card, btnRow);
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(FIELD_STYLE);
        return tf;
    }

    private TextArea styledTextArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setWrapText(true);
        ta.setStyle(
                "-fx-control-inner-background: " + BG_INPUT + ";" +
                        "-fx-text-fill: " + TEXT_HI + ";" +
                        "-fx-prompt-text-fill: " + TEXT_MID + ";" +
                        "-fx-background-color: " + BG_INPUT + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + BORDER_COL + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 12;"
        );
        return ta;
    }

    private Button accentButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_ACCENT);
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_ACCENT_HOVER));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_ACCENT));
        return btn;
    }

    private Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_OUTLINE);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(46);
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_OUTLINE_HOVER));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_OUTLINE));
        return btn;
    }

    public void updateFileList(String fileNamesText) {
        fileLV.getItems().clear();
        if (fileNamesText == null || fileNamesText.isEmpty()) return;
        for (String name : fileNamesText.split(";")) {
            if (!name.isEmpty()) fileLV.getItems().add(name);
        }
    }

    public void zeigeFrageAnIndex(int index){
        frage.setText(fragenArr[index].getFrage());
        String[] antworten = Arrays.copyOf(fragenArr[index].getContent(),fragenArr[index].getContent().length);


        Collections.shuffle(Arrays.asList(antworten));

        for(int i = 0; i < awnserButtons.length; i++) {
            awnserButtons[i].setText(antworten[i]);
            awnserButtons[i].setStyle(BTN_OUTLINE);
            awnserButtons[i].setMouseTransparent(false);
            awnserButtons[i].setOpacity(1.0);
            awnserButtons[i].setOnMouseEntered(e -> ((Button)e.getSource()).setStyle(BTN_OUTLINE_HOVER));
            awnserButtons[i].setOnMouseExited(e  -> ((Button)e.getSource()).setStyle(BTN_OUTLINE));
        }
    }

    public void randomOrder(int anzFragen){
        randomOrder = new Integer[anzFragen];
        for(int i = 0; i < anzFragen; i++){
            randomOrder[i] = i;
        }
        Collections.shuffle(Arrays.asList(randomOrder));
    }

    public boolean checkAwnser(Button btn, int buttonIndex, int fragenNummer){
        boolean [] antwort = fragenArr[fragenNummer].getLoesung();
        String loesung = null;

        for(int i = 0; i < antwort.length; i++){
            if(antwort[i]){
                loesung = fragenArr[fragenNummer].getContent()[i];
            }
        }
        for(int i = 0; i < antwort.length; i++){
            if(btn.getText().equals(loesung)){
                return true;
            }
        }

//        if(buttonIndex == loesung){
//            btn.setStyle(BTN_GREEN);
//            return true;
//        }
        return false;
    }

    public void showRightAwnser(int fragenNummer) {
        boolean[] antwort = fragenArr[fragenNummer].getLoesung();
        String loesung = null;

        for(int i = 0; i < antwort.length; i++){
            if(antwort[i]){
                loesung = fragenArr[fragenNummer].getContent()[i];
            }
        }

        for (int i = 0; i < antwort.length; i++) {
            if(awnserButtons[i].getText().equals(loesung)){
                awnserButtons[i].setStyle(BTN_GREEN);
            }
            else{
                awnserButtons[i].setStyle(BTN_RED);
            }
        }
    }

    public void disableAwnserButtons() {
        for(Button b : awnserButtons) {
            b.setOnMouseEntered(null);
            b.setOnMouseExited(null);
            b.setMouseTransparent(true);
            b.setOpacity(1.0);
        }
    }
}