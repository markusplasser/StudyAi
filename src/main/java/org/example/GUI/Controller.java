package org.example.GUI;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import org.example.manage.Connection;
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
                String anzFragen = o.anzTF.getText();
                String anzAntworten = o.anzAntwTF.getText();
                String quell = o.inputTextTA.getText();

                if(anzFragen.isEmpty() || anzAntworten.isEmpty() || quell.isEmpty() || o.speicherOrtTF.getText().isEmpty()) {
                    new  MyAlertFX(o,
                            Alert.AlertType.ERROR,
                            "Fehlende Eingabe",
                            "Speichern Fehlgeschlagen",
                            "Alle Felder ausfüllen",
                            true,
                            new Image("/images/alerterror.png"),
                            "OK",
                            "Cancel",
                            Color.LIGHTBLUE,
                            Color.WHITE,
                            Color.BLACK);
                    return;
                }

            boolean b = c.saveConnection(quell,Integer.parseInt(anzFragen),Integer.parseInt(anzAntworten),o.speicherOrtTF.getText());

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
                new  MyAlertFX(o,
                        Alert.AlertType.ERROR,
                        "Fehler beim Datei öffnen",
                        "Datei öffnen fehlgeschlagen",
                        "Wähle eine Datei aus",
                        true,
                        new Image("/images/alerterror.png"),
                        "OK",
                        "Cancel",
                        Color.LIGHTBLUE,
                        Color.WHITE,
                        Color.BLACK);
            } else {
                o.root.setCenter(o.fragenVB);

                o.fragenArr = c.returnQuestions(dateiName);

                int anzAntw = o.fragenArr[0].getContent().length;
                o.buildFragenView(anzAntw);


                o.fragenNum = 0;
                o.anzRichtig = 0;
                o.anzFragen = o.fragenArr.length;
                o.randomOrder(o.anzFragen);
                o.zeigeFrageAnIndex(o.randomOrder[o.fragenNum]);
                o.root.setCenter(o.fragenVB);
            }
        }
        if(source == o.itemerstellen){
            o.root.setCenter(o.fragenErstellenVB);
        }

        if(source == o.itemabfragen){
            o.root.setCenter(o.fragenAbfragenVB);
            o.updateFileList(c.returnFileNames());
        }

        if(source == o.nextQuestion) {
            if(!o.antwortGewaehlt){
                new  MyAlertFX(o,
                        Alert.AlertType.INFORMATION,
                        "Information",
                        "Keine Antwort augewählt",
                        "Wähle eine Antwort aus. Du kannst nicht zurück gehen",
                        true,
                        new Image("/images/alertinformation.png"),
                        "OK",
                        "Cancel",
                        Color.LIGHTBLUE,
                        Color.WHITE,
                        Color.BLACK);
                return;
            }
            o.fragenNum++;
            for(int i = 0; i < o.awnserButtons.length; i++){
                o.awnserButtons[i].setMouseTransparent(false);
            }
            if(o.fragenNum < o.fragenArr.length) {
                o.zeigeFrageAnIndex(o.randomOrder[o.fragenNum]);
            } else {
                o.buildErgebniss();
                o.root.setCenter(o.ergebnissVB);
            }
        }

        if(o.awnserButtons != null) {
            for (int i = 0; i < o.awnserButtons.length; i++) {
                if (source == o.awnserButtons[i]) {
                    o.antwortGewaehlt = true;
                    if (o.checkAwnser(o.awnserButtons[i], i,o.randomOrder[o.fragenNum])) {
                        o.anzRichtig++;
                    }
                    o.showRightAwnser(o.randomOrder[o.fragenNum]);
                    o.disableAwnserButtons();
                }
            }
        }

        if(source == o.home){
            o.root.setCenter(o.fragenAbfragenVB);
        }
        if(source == o.again){
            o.fragenArr = c.returnQuestions(o.fragenDateiTF.getText());
            o.fragenNum = 0;
            o.anzRichtig = 0;
            o.anzFragen = o.fragenArr.length;
            o.randomOrder(o.anzFragen);
            o.zeigeFrageAnIndex(o.randomOrder[o.fragenNum]);
            o.root.setCenter(o.fragenVB);
        }
    }
}
