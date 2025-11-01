package com.papervault;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private StudentDAO studentDAO;

    /**
     * Initializes the controller. Called automatically after the FXML file is loaded.
     */
    public void initialize() {
        // Initialize the DAO layer for database interaction
        studentDAO = new StudentDAO();
    }

    /**
     * Handles the action when the Login button is pressed.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String studentId = studentIdField.getText().trim();
        String password = passwordField.getText();

        if (studentId.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both ID and Password.");
            return;
        }

        // 1. Call the DAO to attempt authentication
        Student student = studentDAO.authenticateStudent(studentId, password);

        if (student != null) {
            // 2. Authentication Success: Move to the main dashboard
            messageLabel.setText("Login Successful! Redirecting...");
            
            try {
                // Load the next scene and pass the student object
                loadNextScene("/DashboardView.fxml", student); 
            } catch (IOException e) {
                // Log the error and notify the user if the dashboard fails to load
                System.err.println("Error loading dashboard view: " + e.getMessage());
                e.printStackTrace();
                messageLabel.setText("System error loading dashboard.");
            }
            
        } else {
            // 3. Authentication Failure
            messageLabel.setText("Invalid Student ID or Password.");
            passwordField.clear(); // Clear password for security
        }
    }

    /**
     * Helper method to switch scenes and pass the authenticated student data.
     */
    private void loadNextScene(String fxmlFile, Student student) throws IOException {
        // 1. Get the current stage from the UI component
        Stage currentStage = (Stage) ((Parent) studentIdField.getParent()).getScene().getWindow();
        
        // 2. Load the next FXML
        // Note the leading slash to ensure we load from the classpath root
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        
        // 3. Get the controller for the new scene and pass the Student object
        DashboardController controller = loader.getController();
        controller.setLoggedInUser(student); 
        
        // 4. Set up and show the new scene
        Scene scene = new Scene(root, 1000, 700); // Recommended size for dashboard
        currentStage.setScene(scene);
        currentStage.setTitle("PaperVault - Dashboard for " + student.getName());
        currentStage.setResizable(true);
    }
}