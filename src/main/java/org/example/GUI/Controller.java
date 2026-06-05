package org.example.GUI;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.example.manage.Connection;
import org.example.manage.Fragen_Antworten;
import org.example.GUI.*;
import java.util.Properties;

public class Controller implements EventHandler<Event> {

    final private Oberflaeche o;
    final private Connection c;

    public Controller(Oberflaeche o, Properties p){
        this.o = o;
        c = new Connection(p);
    }
    @Override
    public void handle(Event event) {
        if (event instanceof ActionEvent) {
            handleActionEvent((ActionEvent) event);
        }
        // in case of MouseEvent
        if (event instanceof MouseEvent) {
            handleMouseEvent((MouseEvent) event);
        }
        // in case of KeyEvent
        if (event instanceof KeyEvent) {
            handleKeyEvent((KeyEvent) event);
        }
    }

    private void handleKeyEvent(KeyEvent event) {
        Object source = event.getSource();

        if(source == o.anzTF) {
            if (event.getText().matches("[0-9]")) {
                o.anzTF.setText(o.anzTF.getText() + event.getText());
            }
            event.consume();
        }
    }

    private void handleMouseEvent(MouseEvent event) {
    }

    private void handleActionEvent(ActionEvent event) {
        Object source = event.getSource();

        if(source == o.submit){
            String anz = o.anzTF.getText();
            String quell = o.inputTextTA.getText();
            String anzMöglichkeiten = o.anzAntwortMöglichkeitenProFrage.getText();

            if(anz == null || quell == null){
                return;
            }

            boolean check = checkInputs(anz,quell,anzMöglichkeiten);
            if(check){
                return;
            }
            boolean b = c.saveConnection(quell,
                                        Integer.parseInt(anz),
                                        Integer.parseInt(anzMöglichkeiten),
                                        o.speicherOrtTF.getText());

            if(!b){
                new  MyAlertFX(o,
                        Alert.AlertType.ERROR,
                        "Fehler beim Speichern",
                        "Speichern Fehlgeschlagen",
                        "Versuche es später erneut",
                        true,
                        new Image("/images/alerterror.png"),
                        "OK",
                        "Cancel",
                        Color.LIGHTBLUE,
                        Color.WHITE,
                        Color.BLACK);
            }
        }

        if (source == o.fragenStarten) {
            String dateiName = o.fragenDateiTF.getText();

            if (dateiName.isEmpty()) {
                o.fragenDateiTF.setPromptText("FILENAME AUSFÜLLEN");
                o.fragenDateiTF.setStyle("-fx-prompt-text-fill: red;");
            } else {
                o.fragenDateiTF.setPromptText("Wähle eine Fragen-Datei aus...");
                o.fragenDateiTF.setStyle("-fx-prompt-text-fill: grey;");
                o.fragenDateiTF.clear();
                o.root.setCenter(o.fragenVB);
                Fragen_Antworten[] fragenArr = c.returnQuestions(dateiName);
                System.out.println(fragenArr[0].toString());
            }
        }
        if(source == o.itemerstellen){
            o.root.setCenter(o.fragenErstellenVB);
        }
        if (source == o.itemabfragen) {
            String fileNames = c.returnFileNames();
            o.updateFileList(fileNames);
            o.root.setCenter(o.fragenAbfragenVB);
        }
    }

    private boolean checkInputs(String anz, String quell, String anzMöglichkeiten){

        boolean check = false;
        if(anz.isEmpty()){
            return true;
        }
        if(quell.isEmpty()){
            o.inputTextTA.setText("BITTE AUSFÜLLEN");
            return true;
        }
        if(anzMöglichkeiten.isEmpty()){
            return true;
        }
        if(Integer.parseInt(anz) < 1 || Integer.parseInt(anz) > 15){
            return true;
        }
        if(Integer.parseInt(anzMöglichkeiten) < 2 || Integer.parseInt(anzMöglichkeiten) > 4){
            return true;
        }
        return false;
    }
}
