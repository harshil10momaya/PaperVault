package com.papervault;

import javafx.scene.control.Button;
import java.util.function.Consumer;

// Custom interface needed to pass three arguments (filePath, courseCode, examType)
@FunctionalInterface
interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}

/**
 * ViewModel for displaying Paper data in the TableView and handling the view/download action.
 */
public class PaperViewModel {
    private final int paperId;
    private final String courseCode;
    private final String courseTitle;
    private final int academicYear;
    private final String examType;
    private final Button viewButton;
    private final Button downloadButton; // NEW FIELD
    private final String filePath;

    /**
     * Constructor accepts handlers for both View (Consumer) and Download (TriConsumer).
     */
    public PaperViewModel(Paper paper, Course course, 
                          Consumer<String> viewAction, 
                          TriConsumer<String, String, String> downloadAction) { 
        
        this.paperId = paper.getPaperId();
        this.courseCode = course.getCourseCode(); 
        this.courseTitle = course.getCourseTitle();
        this.academicYear = paper.getAcademicYear();
        this.examType = paper.getExamType();
        this.filePath = paper.getFilePath();

        // Create View Button
        this.viewButton = new Button("View PDF");
        this.viewButton.getStyleClass().add("view-button"); 
        this.viewButton.setOnAction(event -> viewAction.accept(this.filePath));
        
        // NEW: Create Download Button
        this.downloadButton = new Button("Download");
        this.downloadButton.getStyleClass().add("button"); 
        
        // Link the button to the download handler, passing the necessary metadata
        this.downloadButton.setOnAction(event -> 
            downloadAction.accept(this.filePath, this.courseCode, this.examType)); 
    }
    
    // --- Getters (Required by TableView PropertyValueFactory) ---
    public int getPaperId() { return paperId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getAcademicYear() { return academicYear; }
    public String getExamType() { return examType; }
    public Button getViewButton() { return viewButton; }
    public Button getDownloadButton() { return downloadButton; } // NEW GETTER
    public String getFilePath() { return filePath; }
}