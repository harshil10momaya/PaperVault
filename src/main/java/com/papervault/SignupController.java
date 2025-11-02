package com.papervault;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.List;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private ComboBox<Program> programSelector;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private StudentDAO studentDAO;
    private ProgramDAO programDAO;

    public void initialize() {
        studentDAO = new StudentDAO();
        programDAO = new ProgramDAO();
        
        // Load programs into the selector from the database
        List<Program> programs = programDAO.getAllPrograms();
        programSelector.setItems(FXCollections.observableArrayList(programs));
    }
    
    @FXML
    private void handleSignup() {
        String name = nameField.getText().trim();
        // Force uppercase for ID consistency, matching student roll number format
        String studentId = idField.getText().trim().toUpperCase(); 
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        Program selectedProgram = programSelector.getValue();

        // 1. Basic Validation
        if (name.isEmpty() || studentId.isEmpty() || password.isEmpty() || selectedProgram == null) {
            messageLabel.setText("Please fill all required fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters.");
            return;
        }

        // 2. Attempt Signup
        boolean success = studentDAO.signUpStudent(studentId, name, selectedProgram.getProgramId(), password);

        if (success) {
            messageLabel.setText("Registration Successful! Redirecting to login.");
            // Immediately redirect to login after successful registration
            handleBackToLogin(); 
        } else {
            messageLabel.setText("Registration Failed. ID might already exist or a database error occurred.");
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            Stage currentStage = (Stage) idField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            
            // Set the scene size back to the login size
            Scene scene = new Scene(root, 600, 400); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Student Login");
            currentStage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Error redirecting to login view: " + e.getMessage());
        }
    }
}