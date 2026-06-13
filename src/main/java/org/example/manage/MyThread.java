package org.example.manage;
import javafx.application.Platform;
import org.example.GUI.LoadingPopup;
public class MyThread extends Thread {
    @Override
    public void run() {
        Platform.runLater(() -> {
            LoadingPopup loading = new LoadingPopup();
            loading.show();
        });
    }
}
