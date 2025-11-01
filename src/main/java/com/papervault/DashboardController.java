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
    private CourseDAO courseDAO;
    private PaperDAO paperDAO;
    private List<Course> userCourses;
    private Course selectedCourse;

    public void initialize() {
        courseDAO = new CourseDAO();
        paperDAO = new PaperDAO();
        
        // Initialize Table Columns
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("examType"));
        viewCol.setCellValueFactory(new PropertyValueFactory<>("viewButton"));
        
        // Populate Year Filter (Current year and previous 3 years as per requirement)
        int currentYear = java.time.Year.now().getValue();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int i = 0; i < 4; i++) { // Current year and 3 previous years
            years.add(currentYear - i);
        }
        yearFilter.setItems(years);
        
        // Set up search field listener to filter papers instantly
        // Using an immediate change listener for a better experience
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadPapers());
    }

    /**
     * Called by LoginController after successful login.
     */
    public void setLoggedInUser(Student student) {
        this.loggedInUser = student;
        welcomeLabel.setText("Welcome, " + student.getName() + " (" + student.getStudentId() + ")");
        
        // Load courses relevant to the student's program (Core Feature: Course & Subject Mapping)
        loadUserCourses(student.getProgramId());
    }
    
    private void loadUserCourses(int programId) {
        userCourses = courseDAO.getCoursesByProgram(programId);
        
        ObservableList<String> courseStrings = FXCollections.observableArrayList();
        for (Course c : userCourses) {
            courseStrings.add(String.format("[%s] %s (Sem %d)", c.getCourseCode(), c.getCourseTitle(), c.getSemester()));
        }
        
        courseSelector.setItems(courseStrings);
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Find the selected course object
            int selectedIndex = courseSelector.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                selectedCourse = userCourses.get(selectedIndex);
                loadPapers(); // Load papers immediately when a course is selected
            } else {
                selectedCourse = null;
                paperTable.getItems().clear();
            }
        });
        
        // Select the first course if available, for quick dashboard presentation
        if (!userCourses.isEmpty()) {
            courseSelector.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * Fetches and displays papers based on current selections.
     * Implements Core Feature: Search and Filter.
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
            // We pass the Course information explicitly here for display in the table
            viewModels.add(new PaperViewModel(paper, selectedCourse, this::handleViewPaper));
        }
        
        paperTable.setItems(viewModels);
    }
    
    /**
     * Handles the 'View Paper' button click, opening the PDF file.
     * Implements Core Feature: PDF Viewer Access (via system default reader).
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
     * Clears the Year Filter selection and reloads papers.
     */
    @FXML
    private void handleClearYearFilter() {
        yearFilter.getSelectionModel().clearSelection();
        loadPapers();
    }
}