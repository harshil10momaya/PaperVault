package com.papervault;

public class Student {
    private String studentId;
    private int programId;
    private String name;

    public Student(String studentId, int programId, String name) {
        this.studentId = studentId;
        this.programId = programId;
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getProgramId() {
        return programId;
    }

    public String getName() {
        return name;
    }

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