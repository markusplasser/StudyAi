package GUI;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.*;

public class Controller implements EventHandler<Event> {

    final private Oberflaeche o;

    public Controller(Oberflaeche o){
        this.o = o;
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
    }

    private void handleMouseEvent(MouseEvent event) {
    }

    private void handleActionEvent(ActionEvent event) {
        Object source = event.getSource();

        if(source == o.submit){
            System.out.println("Funktioniert");
            String anz = o.anz.getText();
            String quell = o.anz.getText();
            if(anz == null || quell == null){

            }
            if(anz.isEmpty()){
                o.anz.setText("BITTE AUSFÜLLEN");
            }
            if(quell.isEmpty()){
                o.quelltxt.setText("BITTE AUSFÜLLEN");
            }
        }
    }
}
