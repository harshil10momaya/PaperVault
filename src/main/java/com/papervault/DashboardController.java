package com.papervault;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.Desktop; 
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the main student dashboard view (Core Feature: Paper Viewing & Access).
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> courseSelector;
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> yearFilter;
    @FXML private TableView<PaperViewModel> paperTable;
    @FXML private TableColumn<PaperViewModel, String> courseCodeCol;
    @FXML private TableColumn<PaperViewModel, String> subjectCol;
    @FXML private TableColumn<PaperViewModel, String> yearCol;
    @FXML private TableColumn<PaperViewModel, String> typeCol;
    @FXML private TableColumn<PaperViewModel, Button> viewCol;

    private Student loggedInUser;
    private int selectedSemester; // New field to store selected semester
    private CourseDAO courseDAO;
    private PaperDAO paperDAO;
    private List<Course> userCourses;
    private Course selectedCourse;
    private ProgramDAO programDAO;
    
    public void initialize() {
        courseDAO = new CourseDAO();
        paperDAO = new PaperDAO();
        programDAO = new ProgramDAO();
        
        // Initialize Table Columns
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("examType"));
        viewCol.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        
        // Populate Year Filter (Current year and previous 3 years as per requirement)
        int currentYear = java.time.Year.now().getValue();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int i = 0; i < 4; i++) { 
            years.add(currentYear - i);
        }
        yearFilter.setItems(years);
        
        // Set up search field listener to filter papers instantly
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadPapers());
    }

    /**
     * NEW METHOD: Called by SemesterSelectController after successful login and semester choice.
     */
    public void setLoggedInUserAndSemester(Student student, int semester) {
        this.loggedInUser = student;
        this.selectedSemester = semester;
        
        // Look up program name for display
        String programName = programDAO.getAllPrograms().stream()
            .filter(p -> p.getProgramId() == student.getProgramId())
            .findFirst()
            .map(Program::getProgramCode)
            .orElse("Unknown Program");
            
        welcomeLabel.setText(String.format("Welcome, %s (%s)! Viewing %s - Semester %d Papers.", 
            student.getName(), student.getStudentId(), programName, semester));
        
        // Load courses relevant to the student's program and the selected semester
        loadUserCourses(student.getProgramId(), semester);
    }
    
    /**
     * Loads the available courses for the student's program and selected semester.
     */
    private void loadUserCourses(int programId, int semester) {
        // Use the new DAO method to filter by program and semester
        userCourses = courseDAO.getCoursesByProgramAndSemester(programId, semester);
        
        ObservableList<String> courseStrings = FXCollections.observableArrayList();
        for (Course c : userCourses) {
            courseStrings.add(String.format("[%s] %s", c.getCourseCode(), c.getCourseTitle()));
        }
        
        courseSelector.setItems(courseStrings);
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Find the selected course object based on the index
            int selectedIndex = courseSelector.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                selectedCourse = userCourses.get(selectedIndex);
                loadPapers(); // Load papers immediately when a course is selected
            } else {
                selectedCourse = null;
                paperTable.getItems().clear();
            }
        });
        
        // Select the first course if available (e.g., for TCS Sem 5)
        if (!userCourses.isEmpty()) {
            courseSelector.getSelectionModel().selectFirst();
        } else {
            // Show a warning if no courses are found for the selected semester
            welcomeLabel.setText("No courses found for Semester " + semester + ". Please choose another semester.");
        }
    }
    
    /**
     * Fetches and displays papers based on current selections.
     */
    @FXML
    private void loadPapers() {
        if (selectedCourse == null) {
            paperTable.getItems().clear();
            return;
        }

        Integer selectedYear = yearFilter.getValue();
        String searchTerm = searchField.getText();
        
        // Call DAO with filter criteria
        List<Paper> papers = paperDAO.getPapersByCriteria(
            selectedCourse.getCourseId(), 
            selectedYear, 
            searchTerm
        );
        
        // Map data models to view models for table display
        ObservableList<PaperViewModel> viewModels = FXCollections.observableArrayList();
        for (Paper paper : papers) {
            // Pass the selected Course information explicitly here for display in the table
            viewModels.add(new PaperViewModel(paper, selectedCourse, this::handleViewPaper));
        }
        
        paperTable.setItems(viewModels);
    }
    
    /**
     * Handles the 'View Paper' button click, opening the PDF file.
     */
    private void handleViewPaper(String filePath) {
        File file = new File(filePath);
        if (file.exists() && Desktop.isDesktopSupported()) {
            try {
                // Open the file using the system's default PDF reader
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                System.err.println("Error opening file: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not open PDF file. Check file path: " + filePath, ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "File not found at: " + filePath + ". Please contact administration.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**
     * Clears the Year Filter selection.
     */
    @FXML
    private void handleClearYearFilter() {
        yearFilter.getSelectionModel().clearSelection();
        loadPapers();
    }
}