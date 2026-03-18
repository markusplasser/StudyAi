package GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.*;

import javafx.scene.layout.BorderPane;


public class Oberflaeche extends Stage {

    final private Controller controller;

    final MenuItem menuCloseMI;
    public Button submit, antwort1, antwort2, antwort3;
    public TextField quelltxt, anz;

    final Label frage;
    public int width = 1000;
    public int height = 600;

    public Oberflaeche(){
        controller = new Controller(this);
        BorderPane root = new BorderPane();

        VBox fragenErstellen = new VBox(new Label("Erzeuege Fragen"));
        VBox fragenAbfragen = new VBox(new Label("Fragen abfragen"));


        MenuItem itemerstellen = new MenuItem("Fragen erstellen");
        MenuItem itemabfragen = new MenuItem("Fragen abfragen");

        itemerstellen.setOnAction(e -> root.setCenter(fragenErstellen));
        itemabfragen.setOnAction(e -> root.setCenter(fragenAbfragen));


        Menu navigation = new Menu("Navigation");
        navigation.getItems().setAll(itemerstellen,itemabfragen);

        menuCloseMI = new MenuItem("_Close");
        // add controller
        menuCloseMI.addEventHandler(ActionEvent.ACTION, controller);
        menuCloseMI.setMnemonicParsing(true);


        MenuBar menubar = new MenuBar();
        menubar.getMenus().setAll(navigation);
        //menubar.setStyle("-fx-background-color: black; -fx-padding: 5px;");//farbe

        root.setTop(menubar);
        root.setCenter(fragenErstellen);

        /**
         * Fragen erstellen
         */
        quelltxt = new TextField();
        quelltxt.setPromptText("Quelltext");

        anz = new TextField();
        anz.setPromptText("Anzahl der Fragen");

        submit = new Button("Generate");
        submit.addEventHandler(ActionEvent.ACTION,controller);

        fragenErstellen.getChildren().addAll(quelltxt,anz,submit);

        /**
         * Fragen abfragen
         */
        frage = new Label();
        frage.setText("Beispiel Frage die einfach nur da ist um ein Beispiel zu sein ?");
        frage.setMinHeight(70);

        antwort1 = new Button("Antwortmoeglichkeit 1");
        antwort2 = new Button("Antwortmoeglichkeit 2");
        antwort3 = new Button("Antwortmoeglichkeit 3");

        antwort1.setMaxWidth(width/3*2);
        antwort2.setMaxWidth(width/3*2);
        antwort3.setMaxWidth(width/3*2);
        antwort1.setMinHeight(70);
        antwort2.setMinHeight(70);
        antwort3.setMinHeight(70);
        fragenAbfragen.setAlignment(Pos.CENTER);
        fragenAbfragen.getChildren().addAll(frage, antwort1, antwort2, antwort3);


        Scene scene = new Scene(root,width,height);
        setScene(scene);
        show();
        scene.getWindow().sizeToScene();

    }
}
