package com.papervault;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminUploadController {

    @FXML private ComboBox<String> courseComboBox;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> examTypeComboBox;
    @FXML private TextField filePathField;
    @FXML private Label messageLabel;
    @FXML private Label headerLabel;

    private File selectedFile;
    private CourseDAO courseDAO;
    private PaperDAO paperDAO;
    private ProgramDAO programDAO;
    private List<Course> availableCourses;
    
    private int selectedProgramId;
    private int selectedSemester;

    public void initialize() {
        courseDAO = new CourseDAO();
        paperDAO = new PaperDAO();
        programDAO = new ProgramDAO();
        
        ObservableList<String> examTypes = FXCollections.observableArrayList("CA1", "CA2", "SEM");
        examTypeComboBox.setItems(examTypes);
    }
    
    public void setProgramAndSemester(int programId, int semester) {
        this.selectedProgramId = programId;
        this.selectedSemester = semester;
        
        String programCode = programDAO.getAllPrograms().stream()
            .filter(p -> p.getProgramId() == programId)
            .findFirst()
            .map(Program::getProgramCode)
            .orElse("Unknown");
            
        headerLabel.setText(String.format("Uploading Papers for %s - Semester %d", programCode, semester));
        
        loadCourses();
    }

    private void loadCourses() {
        if (selectedProgramId == 0) return; 
        
        availableCourses = courseDAO.getCoursesByProgramAndSemester(selectedProgramId, selectedSemester);
        
        ObservableList<String> courseStrings = FXCollections.observableArrayList();
        for (Course c : availableCourses) {
            courseStrings.add(String.format("[%s] %s", c.getCourseCode(), c.getCourseTitle()));
        }
        courseComboBox.setItems(courseStrings);
        
        if (availableCourses.isEmpty()) {
            messageLabel.setText("Warning: No courses found for this Program and Semester.");
        }
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Paper PDF File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        selectedFile = fileChooser.showOpenDialog(filePathField.getScene().getWindow());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleUploadPaper() {
        if (!validateInput()) {
            return;
        }

        int selectedIndex = courseComboBox.getSelectionModel().getSelectedIndex();
        Course selectedCourse = availableCourses.get(selectedIndex);
        
        int courseId = selectedCourse.getCourseId();
        int academicYear = Integer.parseInt(yearField.getText());
        String examType = examTypeComboBox.getValue();
        String filePath = selectedFile.getAbsolutePath(); 

        boolean success = paperDAO.insertPaper(courseId, academicYear, examType, filePath);

        if (success) {
            messageLabel.setText("Paper metadata uploaded successfully!");
            resetForm();
        } else {
            messageLabel.setText("Upload Failed. Check database for constraint errors.");
        }
    }

    private boolean validateInput() {
        if (courseComboBox.getValue() == null || examTypeComboBox.getValue() == null || filePathField.getText().isEmpty() || yearField.getText().isEmpty()) {
            messageLabel.setText("Please fill out all fields and select a file.");
            return false;
        }
        try {
            int year = Integer.parseInt(yearField.getText());
            if (year < 2000 || year > 2100) { 
                messageLabel.setText("Please enter a valid academic year (e.g., 2025).");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Academic Year must be a number.");
            return false;
        }
        return true;
    }
    
    private void resetForm() {
        courseComboBox.getSelectionModel().clearSelection();
        examTypeComboBox.getSelectionModel().clearSelection();
        yearField.clear();
        filePathField.clear();
        selectedFile = null;
    }
    
    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) messageLabel.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 600, 450); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Student Login");
            currentStage.setResizable(false); 
            
            currentStage.setWidth(600);
            currentStage.setHeight(450); 
            currentStage.setMaximized(false);
            
        } catch (IOException e) {
            System.err.println("Error during admin logout: " + e.getMessage());
        }
    }
}