package com.papervault;

public class Student {
    private String studentId;
    private int programId;
    private String name;
    // Note: We do NOT store the password hash here for security 
    // when passing Student objects around the app.

    // Constructor
    public Student(String studentId, int programId, String name) {
        this.studentId = studentId;
        this.programId = programId;
        this.name = name;
    }

    // --- Getters ---
    public String getStudentId() {
        return studentId;
    }

    public int getProgramId() {
        return programId;
    }

    public String getName() {
        return name;
    }

    // --- Setters (optional, but good practice) ---
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public void setName(String name) {
        this.name = name;
    }
}