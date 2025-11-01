package com.papervault;

import javafx.scene.control.Button;

import java.util.function.Consumer;

/**
 * ViewModel for displaying Paper data in the TableView and handling the view action.
 * Wraps the Paper and Course data necessary for the UI.
 */
public class PaperViewModel {
    private final int paperId;
    private final String courseCode;
    private final String courseTitle;
    private final int academicYear;
    private final String examType;
    private final Button viewButton;
    private final String filePath;

    public PaperViewModel(Paper paper, Course course, Consumer<String> viewAction) {
        this.paperId = paper.getPaperId();
        // Use data from the Course object associated with the paper
        this.courseCode = course.getCourseCode(); 
        this.courseTitle = course.getCourseTitle();
        
        this.academicYear = paper.getAcademicYear();
        this.examType = paper.getExamType();
        this.filePath = paper.getFilePath();

        // Create the interactive button
        this.viewButton = new Button("View PDF");
        this.viewButton.getStyleClass().add("view-button"); 
        this.viewButton.setOnAction(event -> viewAction.accept(this.filePath));
    }

    // --- Getters (Required by TableView PropertyValueFactory) ---
    public int getPaperId() { return paperId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getAcademicYear() { return academicYear; }
    public String getExamType() { return examType; }
    public Button getViewButton() { return viewButton; }
    public String getFilePath() { return filePath; }
}