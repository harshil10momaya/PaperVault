package com.papervault;

import java.time.LocalDateTime;

public class Paper {
    private int paperId;
    private int courseId;
    private int academicYear;
    private String examType; 
    private String filePath;
    private LocalDateTime uploadDate;

    public Paper(int paperId, int courseId, int academicYear, String examType, String filePath, LocalDateTime uploadDate) {
        this.paperId = paperId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.examType = examType;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
    }

    public int getPaperId() { return paperId; }
    public int getCourseId() { return courseId; }
    public int getAcademicYear() { return academicYear; }
    public String getExamType() { return examType; }
    public String getFilePath() { return filePath; }
    public LocalDateTime getUploadDate() { return uploadDate; }
}