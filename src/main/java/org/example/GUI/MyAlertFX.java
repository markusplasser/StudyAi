package org.example.GUI;


import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Leo Fanzott
 *
 */
public class MyAlertFX {

    private Optional<ButtonType> result;

    private Alert alert;

    /**
     *
     * @param window
     * @param alertType
     * @param title
     * @param header
     * @param contentText
     * @param showAndWait
     * @param image
     */
    public MyAlertFX(
            Window window,
            AlertType alertType,
            String title,
            String header,
            String contentText,
            Boolean showAndWait,
            Image image,
            String okBtnText,
            String cancelBtnText,
            Color btnBackgroundColor,
            Color btnSelectedColor,
            Color btnFocusColor) {

        // set alert
        alert = setAlert(window, alertType, title, header, contentText, image);

        // ok button
        Button button = (Button) alert.getDialogPane().lookupButton( ButtonType.OK );
        if (button!=null){
            setButtonLookAndFeel(alert, button, okBtnText, btnBackgroundColor, btnSelectedColor, btnFocusColor);
        }

        // cancel button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.CANCEL );
        if (button!=null)
            setButtonLookAndFeel(alert, button, cancelBtnText, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // apply button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.APPLY );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // close button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.CLOSE );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // finish button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.FINISH );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // next button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.NEXT );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // no button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.NO );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // previous button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.PREVIOUS );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        // yes button
        button = (Button) alert.getDialogPane().lookupButton( ButtonType.YES );
        if (button!=null) setButtonLookAndFeel(alert, button, null, btnBackgroundColor, btnSelectedColor, btnFocusColor);

        if (showAndWait){
            // Show the alert first in a non-blocking way
            alert.setOnShown(e -> {
                double maxWidth = 0;

                // First pass: compute maximum preferred width
                for (ButtonType type : alert.getButtonTypes()) {
                    Button btn = (Button) alert.getDialogPane().lookupButton(type);
                    btn.applyCss();
                    btn.layout();
                    double width = btn.prefWidth(-1);
                    if (width > maxWidth) {
                        maxWidth = width;
                    }
                }

                // Second pass: apply uniform min width
                for (ButtonType type : alert.getButtonTypes()) {
                    Button btn = (Button) alert.getDialogPane().lookupButton(type);
                    btn.setMinWidth(maxWidth + 20); // optional padding
                }
            });

            result = alert.showAndWait();
        }
        else {
            alert.show();
        }
    }


    /**
     * set look and feel of ok and cancel button
     *
     * @param alert
     * @param button
     * @param buttonText
     * @param btnBackgroundColor
     * @param btnSelectedColor
     */
    private void setButtonLookAndFeel(Alert alert, final Button button, String buttonText, Color btnBackgroundColor, Color btnSelectedColor, Color btnFocusColor){
        button.setBackground(new Background(new BackgroundFill(btnBackgroundColor, new CornerRadii(5.0), Insets.EMPTY)));
        button.setOnMouseEntered(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                // change mouse cursor
                button.getScene().getRoot().setCursor(Cursor.HAND);
                SCTools.BorderToNode(button, btnSelectedColor, 10);
            }
        });
        button.setOnMouseExited(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                // change mouse cursor
                button.getScene().getRoot().setCursor(Cursor.DEFAULT);
                button.setEffect(null);
                if (button.isFocused()){
                    SCTools.BorderToNode(button, btnFocusColor, 10);
                }
                else{
                    button.setEffect(null);
                }
            }
        });
        button.focusedProperty().addListener((obs, oldVal, newVal) ->
        {	if (button.isFocused()){
            SCTools.BorderToNode(button, btnFocusColor, 10);
        }
        else{
            button.setEffect(null);
        }
        });

        if (buttonText!=null) button.setText(buttonText);

    }


    /**
     *
     * @param window
     * @param alertType
     * @param title
     * @param header
     * @param contentText
     * @param image
     * @return
     */
    private Alert setAlert(Window window, AlertType alertType, String title, String header, String contentText,
                           Image image){
        alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contentText);
        alert.initOwner(window);
        alert.initStyle(StageStyle.DECORATED);


        ImageView iv=null;
        switch (alertType){
            case ERROR:
                iv=new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/alerterror.png"))));
                break;
            case INFORMATION:
                iv=new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/alertinformation.png"))));
                break;
            case CONFIRMATION:
                iv=new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/alertconfirmation.png"))));
                break;
            default:
        }
        assert iv != null;
        iv.setFitWidth(30);
        iv.setFitHeight(30);
        alert.setGraphic(iv);

        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinWidth(Region.USE_PREF_SIZE));
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMaxWidth(Region.USE_PREF_SIZE));

        alert.getDialogPane().setBackground(new Background(new BackgroundFill(Color.GAINSBORO, CornerRadii.EMPTY, Insets.EMPTY)));

        Stage stage=(Stage) alert.getDialogPane().getScene().getWindow();

        // application image
        if (image != null) {
            stage.getIcons().add(image);
        }

        // close over x not allowed
        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent event) {
                // TODO Auto-generated method stub
                event.consume();
            }

        });

        stage.centerOnScreen();
        // set alert always on top
        stage.setAlwaysOnTop(true);

        return alert;
    }

    /**
     * @return the result
     */
    public Optional<ButtonType> getResult() {
        return result;
    }

    /**
     * @return the alert
     */
    public Alert getAlert() {
        return alert;
    }

}
