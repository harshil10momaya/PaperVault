package com.papervault;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    
    // --- MVP Admin Credentials (Hardcoded for simplicity) ---
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "adminpass"; // CHANGE THIS LATER!

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            messageLabel.setText("Admin Login Successful! Redirecting...");
            try {
                loadAdminDashboard("/AdminUploadView.fxml");
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error loading admin dashboard.");
            }
        } else {
            messageLabel.setText("Invalid Admin Credentials.");
            passwordField.clear();
        }
    }

    private void loadAdminDashboard(String fxmlFile) throws IOException {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 800, 600);
        currentStage.setScene(scene);
        currentStage.setTitle("PaperVault - Admin Upload");
        currentStage.setResizable(true);
    }
}