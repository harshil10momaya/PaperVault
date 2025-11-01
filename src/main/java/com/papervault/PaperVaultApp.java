package com.papervault;

import javafx.application.Application;
import javafx.stage.Stage;

public class PaperVaultApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // This is the starting point for your JavaFX application.
        // We will load the Login Screen FXML here later.
        stage.setTitle("PaperVault - Starting Soon!");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}