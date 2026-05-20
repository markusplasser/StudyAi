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
        c.p
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
            if(anz == null || quell == null){
                return;
            }
            if(anz.isEmpty()){
                o.anzTF.setText("BITTE AUSFÜLLEN");
            }
            if(quell.isEmpty()){
                o.inputTextTA.setText("BITTE AUSFÜLLEN");
            }

            c.saveConnection(quell,Integer.parseInt(anz),3,"test");
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
            }
        }
        if(source == o.itemerstellen){
            o.root.setCenter(o.fragenErstellenVB);
        }
        if(source == o.itemabfragen){
            o.root.setCenter(o.fragenAbfragenVB);
        }
    }
}
