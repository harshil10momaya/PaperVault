package com.papervault;

import javafx.event.ActionEvent; // Added import for the missing method parameter
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.regex.Pattern; 

public class AdminUploadController {

    @FXML private ComboBox<String> courseComboBox;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> examTypeComboBox;
    @FXML private TextField filePathField;
    @FXML private Label messageLabel;
    @FXML private Label headerLabel;

    // FXML fields for NEW course creation
    @FXML private VBox existingCoursePane;
    @FXML private VBox newCoursePane;
    @FXML private TextField newCourseCodeField;
    @FXML private TextField newCourseTitleField;


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
        
        // Add listener to the ComboBox to handle selection change on its own
        // If the FXML is using onAction="#handleCourseSelectionChange", this explicit listener is usually not needed.
        // If you were using onSelect, you might use courseComboBox.getSelectionModel().selectedItemProperty().addListener(...)
    }
    
    /**
     * Called by AdminSelectController to set the context for the upload.
     */
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
            messageLabel.setText("Warning: No courses found. Please add a new subject below.");
            handleToggleNewCourseInput(); // Automatically switch to new course input
        } else {
            messageLabel.setText(null);
            handleToggleNewCourseInput(false); // Ensure starting in selector mode
        }
    }
    
    /**
     * FIX FOR LOAD EXCEPTION ERROR: 
     * Handles the 'onAction' event from the course selection control (likely a ComboBox).
     * This method was missing, causing the FXML load to fail.
     */
    @FXML
    private void handleCourseSelectionChange(ActionEvent event) {
        // Optional: Add logic to react to the change, e.g., clear a previously entered year
        messageLabel.setText(null); // Clear previous error messages when a new course is selected
        System.out.println("Course selection changed.");
        
        // This method can be left empty if the FXML just needs to resolve it, 
        // or add logic if other parts of the view need to be updated.
    }
    
    /**
     * Toggles visibility between the existing course selector and the new course input fields.
     */
    @FXML
    private void handleToggleNewCourseInput() {
        handleToggleNewCourseInput(newCoursePane.isVisible());
    }

    private void handleToggleNewCourseInput(boolean currentlyVisible) {
        boolean nextState = !currentlyVisible;
        existingCoursePane.setVisible(!nextState);
        existingCoursePane.setManaged(!nextState);
        newCoursePane.setVisible(nextState);
        newCoursePane.setManaged(nextState);
        messageLabel.setText(null);
    }
    
    /**
     * Attempts to find the Course ID, creating the course if the new course pane is active.
     * @return The valid course ID, or -1 on failure.
     */
    private int getOrCreateCourseId() {
        if (newCoursePane.isVisible()) {
            String newCode = newCourseCodeField.getText().trim().toUpperCase();
            String newTitle = newCourseTitleField.getText().trim();

            if (newCode.isEmpty() || newTitle.isEmpty()) {
                messageLabel.setText("Error: New course code and title cannot be empty.");
                return -1;
            }
            // Basic code validation (alphanumeric and short)
            if (!Pattern.matches("[A-Z0-9]{3,8}", newCode)) {
                messageLabel.setText("Error: Course Code must be 3-8 uppercase letters/numbers.");
                return -1;
            }
            
            // Try to insert the new course
            int newId = courseDAO.insertNewCourse(selectedProgramId, selectedSemester, newCode, newTitle);
            if (newId == -1) {
                messageLabel.setText("Error: Failed to create new subject. Code might already exist.");
                return -1;
            }
            // Successfully created new course
            return newId;

        } else if (courseComboBox.getValue() != null) {
            // Use existing course
            int selectedIndex = courseComboBox.getSelectionModel().getSelectedIndex();
            return availableCourses.get(selectedIndex).getCourseId();
        }

        messageLabel.setText("Error: Please select an existing course or add a new subject.");
        return -1;
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
        int courseId = getOrCreateCourseId();
        if (courseId == -1) {
            return;
        }

        if (filePathField.getText().isEmpty() || yearField.getText().isEmpty() || examTypeComboBox.getValue() == null) {
            messageLabel.setText("Please fill out all metadata fields and select a PDF file.");
            return;
        }

        // --- Standard Upload Logic ---
        
        try {
            int academicYear = Integer.parseInt(yearField.getText());
            String examType = examTypeComboBox.getValue();
            String filePath = selectedFile.getAbsolutePath(); 

            boolean success = paperDAO.insertPaper(courseId, academicYear, examType, filePath);

            if (success) {
                messageLabel.setText("✅ Paper and subject (if new) successfully uploaded!");
                // Refresh the course list and reset form
                loadCourses();
                resetForm();
            } else {
                messageLabel.setText("❌ Upload Failed. Paper metadata (Course/Year/Type) may be a duplicate.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Academic Year must be a valid number.");
        } catch (NullPointerException e) {
            messageLabel.setText("Error: Please select a file and check all inputs.");
        }
    }

    private boolean validateInput() {
        // Redundant method, main validation is in getOrCreateCourseId and handleUploadPaper
        return true; 
    }
    
    private void resetForm() {
        courseComboBox.getSelectionModel().clearSelection();
        examTypeComboBox.getSelectionModel().clearSelection();
        yearField.clear();
        filePathField.clear();
        newCourseCodeField.clear();
        newCourseTitleField.clear();
        selectedFile = null;
        // Keep in existing course mode after successful upload
        handleToggleNewCourseInput(true);
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