package com.papervault;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PaperVaultApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the Login Screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PaperVaultApp.class.getResource("/LoginView.fxml"));
            Parent root = fxmlLoader.load();
            
            Scene scene = new Scene(root);
            
            stage.setTitle("PaperVault - Student Login");
            stage.setScene(scene);
            stage.setResizable(false);
            
            // FIX: Explicitly set the size at launch to match FXML height
            stage.setWidth(600); 
            stage.setHeight(450); 
            
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}