package com.papervault;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "adminpass";

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            messageLabel.setText("Admin Login Successful! Redirecting...");
            try {
                loadAdminSelectScreen("/AdminSelectView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error loading admin dashboard.");
            }
        } else {
            messageLabel.setText("Invalid Admin Credentials.");
            passwordField.clear();
        }
    }

    private void loadAdminSelectScreen(String fxmlFile) throws IOException {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 650, 450);
        currentStage.setScene(scene);
        currentStage.setTitle("Admin Select Program/Semester");
        currentStage.setResizable(false);
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 600, 450); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Student Login");
            currentStage.setResizable(false); 
        } catch (IOException e) {
            System.err.println("Error redirecting to login view: " + e.getMessage());
        }
    }
}