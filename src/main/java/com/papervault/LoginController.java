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

public class LoginController {

    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private StudentDAO studentDAO;

    public void initialize() {
        studentDAO = new StudentDAO();
    }

    /**
     * Handles the action when the Student Login button is pressed.
     * Redirects to the Semester Select screen on success.
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
            // 2. Authentication Success: Move to the new Semester Selection Screen
            messageLabel.setText("Login Successful! Redirecting...");
            
            try {
                loadSemesterSelectScene("/SemesterSelectView.fxml", student); 
            } catch (IOException e) {
                System.err.println("Error loading semester select view: " + e.getMessage());
                e.printStackTrace();
                messageLabel.setText("System error loading next screen.");
            }
            
        } else {
            // 3. Authentication Failure
            messageLabel.setText("Invalid Student ID or Password.");
            passwordField.clear(); 
        }
    }

    /**
     * Handles the action to switch to the student sign-up screen.
     */
    @FXML
    private void handleSignupAccess(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Parent) studentIdField.getParent()).getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignupView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 600, 500);
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Student Sign-up");
            currentStage.setResizable(false);
            currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Error accessing sign-up panel: " + e.getMessage());
        }
    }

    /**
     * Handles the action when the Admin Access button is pressed.
     */
    @FXML
    private void handleAdminAccess(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Parent) studentIdField.getParent()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminLoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 500, 350);
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Admin Login");
            currentStage.setResizable(false);
            currentStage.show();
            
        } catch (IOException e) {
            System.err.println("Error accessing admin panel: " + e.getMessage());
        }
    }

    /**
     * Helper method to switch to the Semester Selection scene.
     */
    private void loadSemesterSelectScene(String fxmlFile, Student student) throws IOException {
        Stage currentStage = (Stage) ((Parent) studentIdField.getParent()).getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        
        // Pass the student object to the SemesterSelectController
        SemesterSelectController controller = loader.getController();
        controller.setLoggedInUser(student); 
        
        Scene scene = new Scene(root, 600, 400); 
        currentStage.setScene(scene);
        currentStage.setTitle("PaperVault - Select Semester");
        currentStage.setResizable(false);
    }
}