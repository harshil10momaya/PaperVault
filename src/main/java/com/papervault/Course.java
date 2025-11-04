package com.papervault;

public class Course {
    private int courseId;
    private int programId;
    private String courseCode;
    private String courseTitle;
    private int semester;

    public Course(int courseId, int programId, String courseCode, String courseTitle, int semester) {
        this.courseId = courseId;
        this.programId = programId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.semester = semester;
    }

    public int getCourseId() { return courseId; }
    public int getProgramId() { return programId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getSemester() { return semester; }
}