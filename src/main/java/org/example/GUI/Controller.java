package org.example.GUI;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.*;
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
            if(anzFragen == null || quell == null){
                return;
            }
            if(anzFragen.isEmpty()){
                o.anzTF.setText("BITTE AUSFÜLLEN");
            }
            if(anzAntworten == null){
                return;
            }
            if(anzAntworten.isEmpty()){
                o.anzTF.setText("BITTE AUSFÜLLEN");
            }
            if(quell.isEmpty()){
                o.inputTextTA.setText("BITTE AUSFÜLLEN");
            }

            boolean b = c.saveConnection(quell,Integer.parseInt(anzFragen),Integer.parseInt(anzAntworten),o.speicherOrtTF.getText());

            if(!b){
                //MyAlertFX alertFX = new  MyAlertFX();
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
                o.root.setCenter(o.fragenVB);

                o.fragenArr = c.returnQuestions(o.fragenDateiTF.getText());

                int anzAntw = o.fragenArr[0].getContent().length;
                o.buildFragenView(anzAntw);

                o.fragenNum = 0;
                o.anzRichtig = 0;
                o.anzFragen = o.fragenArr.length;
                o.zeigeFrageAnIndex(0);
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
            o.fragenNum++;
            for(int i = 0; i < o.awnserButtons.length; i++){
                o.awnserButtons[i].setMouseTransparent(false);
            }
            if(o.fragenNum < o.fragenArr.length) {
                o.zeigeFrageAnIndex(o.fragenNum);

                o.antwort1.setStyle(o.BTN_OUTLINE);
                o.antwort2.setStyle(o.BTN_OUTLINE);
                o.antwort3.setStyle(o.BTN_OUTLINE);

            } else {
                o.buildErgebniss();
                o.root.setCenter(o.ergebnissVB);
            }
        }

        if(o.awnserButtons != null) {
            for (int i = 0; i < o.awnserButtons.length; i++) {
                if (source == o.awnserButtons[i]) {
                    if (o.checkAwnser(o.awnserButtons[i], i, o.fragenNum)) {
                        o.anzRichtig++;
                    }
                    o.showRightAwnser(o.fragenNum);
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
            o.zeigeFrageAnIndex(0);
            o.root.setCenter(o.fragenVB);
        }
    }
}
