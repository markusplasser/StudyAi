package GUI;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import org.example.Connection;

public class Controller implements EventHandler<Event> {

    final private Oberflaeche o;
    final private Connection c;

    public Controller(Oberflaeche o){
        this.o = o;
        c = new  Connection();
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
            String quell = o.anzTF.getText();
            if(anz == null || quell == null){
                return;
            }
            if(anz.isEmpty()){
                o.anzTF.setText("BITTE AUSFÜLLEN");
            }
            if(quell.isEmpty()){
                o.quelltxt.setText("BITTE AUSFÜLLEN");
            }
            String time = java.time.LocalTime.now().toString();
            c.saveConnection(quell,Integer.parseInt(anz),3,time);
        }
    }
}
