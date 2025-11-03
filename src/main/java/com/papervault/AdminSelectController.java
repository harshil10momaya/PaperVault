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
import java.util.List;

public class AdminSelectController {
    
    @FXML private ComboBox<Program> programSelector;
    @FXML private ComboBox<Integer> semesterSelector;
    @FXML private Label messageLabel;
    
    private ProgramDAO programDAO;
    private List<Program> availablePrograms;
    
    public void initialize() {
        programDAO = new ProgramDAO();
        
        // 1. Populate Program Selector
        availablePrograms = programDAO.getAllPrograms();
        programSelector.setItems(FXCollections.observableArrayList(availablePrograms));
        
        // 2. Populate Semester Selector
        semesterSelector.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)));
    }
    
    @FXML
    private void handleProceedToUpload() {
        Program selectedProgram = programSelector.getValue();
        Integer selectedSemester = semesterSelector.getValue();
        
        if (selectedProgram == null || selectedSemester == null) {
            messageLabel.setText("Please select both a Program and a Semester.");
            return;
        }

        try {
            Stage currentStage = (Stage) programSelector.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUploadView.fxml"));
            Parent root = loader.load();
            
            // Pass the selected criteria to the upload controller
            AdminUploadController controller = loader.getController();
            controller.setProgramAndSemester(selectedProgram.getProgramId(), selectedSemester); 
            
            Scene scene = new Scene(root, 1000, 700); // Set a good default size
            currentStage.setScene(scene);
            currentStage.setTitle("Admin Upload - " + selectedProgram.getProgramCode() + " Sem " + selectedSemester);
            currentStage.setResizable(true);
            
            // FIX 1: Maximize the window for a full-size experience
            currentStage.setMaximized(true); 

        } catch (IOException e) {
            messageLabel.setText("Error loading upload form. Check console.");
            System.err.println("FATAL ERROR loading AdminUploadView (Check FXML syntax): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        // ... (Logout logic remains the same) ...
        try {
            Stage currentStage = (Stage) programSelector.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 600, 450); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Student Login");
            currentStage.setResizable(false); 
            currentStage.setWidth(600);
            currentStage.setHeight(450); 
            currentStage.setMaximized(false); // Ensure main login isn't maximized
            
        } catch (IOException e) {
            System.err.println("Error during admin logout: " + e.getMessage());
        }
    }
}