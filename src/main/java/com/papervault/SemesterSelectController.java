package com.papervault;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.Arrays;

public class SemesterSelectController {
    
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<Integer> semesterSelector;
    
    private Student loggedInUser;
    
    public void initialize() {
        // Populate the semester selector with all possible academic semesters (1-8)
        semesterSelector.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)));
    }
    
    /**
     * Called by LoginController to pass the authenticated student object.
     */
    public void setLoggedInUser(Student student) {
        this.loggedInUser = student;
        welcomeLabel.setText("Hello, " + student.getName() + "! Which semester's papers are you looking for?");
    }
    
    /**
     * Handles proceeding to the main Dashboard.
     * FIX: Ensures the Dashboard stage is maximized for 'full screen'.
     */
    @FXML
    private void handleProceedToDashboard() {
        Integer selectedSemester = semesterSelector.getValue();
        if (selectedSemester == null) {
            welcomeLabel.setText("Please select a semester to proceed.");
            return;
        }

        try {
            Stage currentStage = (Stage) semesterSelector.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardView.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setLoggedInUserAndSemester(loggedInUser, selectedSemester); 
            
            // Use a large starting size
            Scene scene = new Scene(root, 1200, 800); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Semester " + selectedSemester + " Papers");
            currentStage.setResizable(true);
            
            currentStage.setMaximized(true); 

        } catch (IOException e) {
            System.err.println("Error loading Dashboard View: " + e.getMessage());
            e.printStackTrace();
        }
    }
}