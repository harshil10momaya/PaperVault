package com.papervault;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser; // Required for file chooser dialog

import java.awt.Desktop; 
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the main student dashboard view.
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> courseSelector;
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> yearFilter;
    @FXML private ComboBox<String> examTypeFilter; 
    @FXML private TableView<PaperViewModel> paperTable;
    @FXML private TableColumn<PaperViewModel, String> courseCodeCol;
    @FXML private TableColumn<PaperViewModel, String> subjectCol;
    @FXML private TableColumn<PaperViewModel, String> yearCol;
    @FXML private TableColumn<PaperViewModel, String> typeCol;
    @FXML private TableColumn<PaperViewModel, Button> viewCol;
    @FXML private TableColumn<PaperViewModel, Button> downloadCol; // NEW FXML FIELD

    private Student loggedInUser;
    private int selectedSemester; 
    private CourseDAO courseDAO;
    private PaperDAO paperDAO;
    private ProgramDAO programDAO;
    private List<Course> userCourses;
    private Course selectedCourse;
    
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
        downloadCol.setCellValueFactory(new PropertyValueFactory<>("downloadButton")); // NEW INITIALIZATION
        
        // Populate Year Filter (Current year and 3 previous years)
        int currentYear = java.time.Year.now().getValue();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int i = 0; i < 4; i++) { 
            years.add(currentYear - i);
        }
        yearFilter.setItems(years);
        
        // Populate Exam Type Filter
        ObservableList<String> examTypes = FXCollections.observableArrayList("CA1", "CA2", "SEM");
        examTypeFilter.setItems(examTypes);
        
        // Set up search field listener to filter papers instantly
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadPapers());
    }

    /**
     * Called by SemesterSelectController after successful login and semester choice.
     */
    public void setLoggedInUserAndSemester(Student student, int semester) {
        this.loggedInUser = student;
        this.selectedSemester = semester;
        
        String programName = programDAO.getAllPrograms().stream()
            .filter(p -> p.getProgramId() == student.getProgramId())
            .findFirst()
            .map(Program::getProgramCode)
            .orElse("Unknown Program");
            
        welcomeLabel.setText(String.format("Welcome, %s (%s)! Viewing %s - Semester %d Papers.", 
            student.getName(), student.getStudentId(), programName, semester));
        
        loadUserCourses(student.getProgramId(), semester);
    }
    
    private void loadUserCourses(int programId, int semester) {
        userCourses = courseDAO.getCoursesByProgramAndSemester(programId, semester);
        
        ObservableList<String> courseStrings = FXCollections.observableArrayList();
        for (Course c : userCourses) {
            courseStrings.add(String.format("[%s] %s", c.getCourseCode(), c.getCourseTitle()));
        }
        
        courseSelector.setItems(courseStrings);
        courseSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            int selectedIndex = courseSelector.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                selectedCourse = userCourses.get(selectedIndex);
                loadPapers(); 
            } else {
                selectedCourse = null;
                paperTable.getItems().clear();
            }
        });
        
        if (!userCourses.isEmpty()) {
            courseSelector.getSelectionModel().selectFirst();
        } else {
            welcomeLabel.setText("No courses found for Semester " + semester + ". Please select a different semester.");
        }
    }
    
    /**
     * Fetches and displays papers based on current selections and filters.
     */
    @FXML
    private void loadPapers() {
        if (selectedCourse == null) {
            paperTable.getItems().clear();
            return;
        }

        Integer selectedYear = yearFilter.getValue();
        String selectedExamType = examTypeFilter.getValue(); 
        String searchTerm = searchField.getText();
        
        List<Paper> papers = paperDAO.getPapersByCriteria(
            selectedCourse.getCourseId(), 
            selectedYear, 
            selectedExamType, 
            searchTerm
        );
        
        ObservableList<PaperViewModel> viewModels = FXCollections.observableArrayList();
        for (Paper paper : papers) {
            // Updated to pass the download handler method
            viewModels.add(new PaperViewModel(paper, selectedCourse, 
                                              this::handleViewPaper, 
                                              this::handleDownloadPaper)); 
        }
        
        paperTable.setItems(viewModels);
    }
    
    private void handleViewPaper(String filePath) {
        File file = new File(filePath);
        if (file.exists() && Desktop.isDesktopSupported()) {
            try {
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
     * Handles the 'Download' button click, copying the file to a location chosen by the user.
     */
    private void handleDownloadPaper(String sourceFilePath, String courseCode, String examType) {
        File sourceFile = new File(sourceFilePath);
        
        if (!sourceFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Source file not found.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // 1. Open FileChooser for destination
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Previous Year Paper");
        
        // Suggest a filename (e.g., 23XT51_CA1_2025.pdf)
        String suggestedFileName = String.format("%s_%s_%d.pdf", 
                                                courseCode, 
                                                examType, 
                                                yearFilter.getValue() != null ? yearFilter.getValue() : java.time.Year.now().getValue());
        fileChooser.setInitialFileName(suggestedFileName);
        
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        // Show save dialog
        Stage stage = (Stage) paperTable.getScene().getWindow();
        File destinationFile = fileChooser.showSaveDialog(stage);

        if (destinationFile != null) {
            try {
                // 2. Perform the file copy
                java.nio.file.Files.copy(sourceFile.toPath(), destinationFile.toPath(), 
                                         java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                         
                Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                                        "Download successful! File saved to:\n" + destinationFile.getAbsolutePath(), 
                                        ButtonType.OK);
                alert.showAndWait();
                
            } catch (IOException e) {
                System.err.println("Error copying file: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Download failed due to file system error.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
    
    // --- Navigation and Filter Handlers ---
    
    @FXML
    private void handleBackToSemester() {
        try {
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SemesterSelectView.fxml"));
            Parent root = loader.load();
            
            SemesterSelectController controller = loader.getController();
            controller.setLoggedInUser(loggedInUser); 
            
            Scene scene = new Scene(root, 600, 400); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Select Semester");
            currentStage.setResizable(true); 
            
        } catch (IOException e) {
            System.err.println("Error during navigation back to semester select: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearYearFilter() {
        yearFilter.getSelectionModel().clearSelection();
        loadPapers();
    }
    
    @FXML
    private void handleClearExamFilter() {
        examTypeFilter.getSelectionModel().clearSelection();
        loadPapers();
    }
    
    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 600, 450); 
            currentStage.setScene(scene);
            currentStage.setTitle("PaperVault - Student Login");
            currentStage.setResizable(false); 
            
            currentStage.setWidth(600);
            currentStage.setHeight(450); 
            
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
}