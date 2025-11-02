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
    
    // Hardcoded Program ID for TCS (Program ID 1 is assumed from the SQL insert)
    private static final int ADMIN_PROGRAM_ID = 1; 

    public void initialize() {
        courseDAO = new CourseDAO();
        paperDAO = new PaperDAO();
        
        // Populate Exam Type ComboBox
        ObservableList<String> examTypes = FXCollections.observableArrayList("CA1", "CA2", "SEM");
        examTypeComboBox.setItems(examTypes);

        // Load Courses for the ComboBox using the utility method (Fixes Error 2)
        loadCourses();
    }

    /**
     * Loads ALL available courses for the Admin's default program (TCS).
     */
    private void loadCourses() {
        // Call the utility DAO method we just created
        availableCourses = courseDAO.getAllCoursesByProgram(ADMIN_PROGRAM_ID);
        
        ObservableList<String> courseStrings = FXCollections.observableArrayList();
        for (Course c : availableCourses) {
            // Display: [23XT51] Theory of Computing (Sem 5)
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
        // Crucial: This path must be correct and accessible by the student view later.
        String filePath = selectedFile.getAbsolutePath(); 

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
        // Keep the success message visible briefly
    }
}