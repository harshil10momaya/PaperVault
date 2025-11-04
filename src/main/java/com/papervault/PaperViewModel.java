package com.papervault;

import javafx.scene.control.Button;
import java.util.function.Consumer;

@FunctionalInterface
interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}

@FunctionalInterface
interface BookmarkToggleHandler {
    boolean toggle(int paperId, boolean isFavorited);
}

public class PaperViewModel {
    private final int paperId;
    private final String courseCode;
    private final String courseTitle;
    private final int academicYear;
    private final String examType;
    private final Button viewButton;
    private final Button downloadButton; 
    private final Button bookmarkButton;
    private final String filePath;
    private boolean isFavorited;

    public PaperViewModel(Paper paper, Course course, boolean isFavorited, 
                          Consumer<String> viewAction, 
                          TriConsumer<String, String, String> downloadAction,
                          BookmarkToggleHandler bookmarkAction) { 
        
        this.paperId = paper.getPaperId();
        this.courseCode = course.getCourseCode(); 
        this.courseTitle = course.getCourseTitle();
        this.academicYear = paper.getAcademicYear();
        this.examType = paper.getExamType();
        this.filePath = paper.getFilePath();
        this.isFavorited = isFavorited;

        this.viewButton = new Button("View PDF");
        this.viewButton.getStyleClass().add("view-button"); 
        this.viewButton.setOnAction(event -> viewAction.accept(this.filePath));
        
        this.downloadButton = new Button("Download");
        this.downloadButton.getStyleClass().add("button"); 
        this.downloadButton.setOnAction(event -> 
            downloadAction.accept(this.filePath, this.courseCode, this.examType)); 
            
        this.bookmarkButton = new Button(isFavorited ? "★ Bookmarked" : "☆ Bookmark");
        this.bookmarkButton.getStyleClass().add(isFavorited ? "bookmark-active" : "bookmark-inactive"); 

        this.bookmarkButton.setOnAction(event -> {
            boolean success = bookmarkAction.toggle(this.paperId, this.isFavorited);
            if (success) {
                this.isFavorited = !this.isFavorited;
                updateBookmarkButtonStyle();
            }
        });
    }
    
    private void updateBookmarkButtonStyle() {
        if (this.isFavorited) {
            this.bookmarkButton.setText("★ Bookmarked");
            this.bookmarkButton.getStyleClass().setAll("button", "bookmark-active");
        } else {
            this.bookmarkButton.setText("☆ Bookmark");
            this.bookmarkButton.getStyleClass().setAll("button", "bookmark-inactive");
        }
    }
    
    public int getPaperId() { return paperId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getAcademicYear() { return academicYear; }
    public String getExamType() { return examType; }
    public Button getViewButton() { return viewButton; }
    public Button getDownloadButton() { return downloadButton; }
    public Button getBookmarkButton() { return bookmarkButton; }
    public String getFilePath() { return filePath; }
}