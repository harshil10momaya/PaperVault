package com.papervault;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminUploadController {

    @FXML private ComboBox<String> courseComboBox;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> examTypeComboBox;
    @FXML private TextField filePathField;
    @FXML private Label messageLabel;

    private File selectedFile;
    private CourseDAO courseDAO;
    private PaperDAO paperDAO;
    private List<Course> availableCourses;
    
    // Hardcoded Program ID for simplicity, assuming Admin manages Program ID 1 initially
    private static final int ADMIN_PROGRAM_ID = 1; 

    public void initialize() {
        courseDAO = new CourseDAO();
        paperDAO = new PaperDAO();
        
        // Populate Exam Type ComboBox
        ObservableList<String> examTypes = FXCollections.observableArrayList("CA1", "CA2", "SEM");
        examTypeComboBox.setItems(examTypes);

        // Load Courses for the ComboBox (We assume the admin belongs to Program 1)
        loadCourses();
    }

    private void loadCourses() {
        // Fetch courses for the admin's program. For MVP, we use the first program ID (1).
        availableCourses = courseDAO.getCoursesByProgram(ADMIN_PROGRAM_ID);
        
        ObservableList<String> courseStrings = FXCollections.observableArrayList();
        for (Course c : availableCourses) {
            courseStrings.add(String.format("[%s] %s (Sem %d)", c.getCourseCode(), c.getCourseTitle(), c.getSemester()));
        }
        courseComboBox.setItems(courseStrings);
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
        // 1. Validation and Data Extraction
        if (!validateInput()) {
            return;
        }

        // Get selected Course object
        int selectedIndex = courseComboBox.getSelectionModel().getSelectedIndex();
        Course selectedCourse = availableCourses.get(selectedIndex);
        
        int courseId = selectedCourse.getCourseId();
        int academicYear = Integer.parseInt(yearField.getText());
        String examType = examTypeComboBox.getValue();
        String filePath = selectedFile.getAbsolutePath(); // Use the absolute path for storage

        // 2. Insert into Database
        boolean success = paperDAO.insertPaper(courseId, academicYear, examType, filePath);

        if (success) {
            messageLabel.setText("✅ Paper metadata uploaded successfully!");
            // Reset form for next upload
            resetForm();
        } else {
            messageLabel.setText("❌ Upload Failed. Check database for constraint errors (e.g., duplicate paper).");
        }
    }

    private boolean validateInput() {
        if (courseComboBox.getValue() == null || examTypeComboBox.getValue() == null || filePathField.getText().isEmpty() || yearField.getText().isEmpty()) {
            messageLabel.setText("Please fill out all fields and select a file.");
            return false;
        }
        try {
            int year = Integer.parseInt(yearField.getText());
            if (year < 2000 || year > 2100) { // Basic year validation
                messageLabel.setText("Please enter a valid academic year (e.g., 2023).");
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
}