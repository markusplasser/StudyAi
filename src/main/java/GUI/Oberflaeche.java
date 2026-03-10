package GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.*;

import javafx.scene.layout.BorderPane;


public class Oberflaeche extends Stage {

    final private Controller controller;

    final MenuItem menuCloseMI;
    public Button submit;
    public TextField quelltxt, anz;

    public Oberflaeche(){
        controller = new Controller(this);
        BorderPane root = new BorderPane();

        VBox fragenErstellen = new VBox(new Label("Erzeuege Fragen"));
        VBox fragenAbfragen = new VBox(new Label("Fargen abfragen"));


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

        quelltxt = new TextField();
        quelltxt.setPromptText("Quelltext");

        anz = new TextField();
        anz.setPromptText("Anzahl der Fragen");

        submit = new Button("Generate");
        submit.addEventHandler(ActionEvent.ACTION,controller);

        fragenErstellen.getChildren().addAll(quelltxt,anz,submit);



        Scene scene = new Scene(root,1000,600);
        setScene(scene);
        show();
        scene.getWindow().sizeToScene();

    }
}
