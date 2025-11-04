package com.papervault;

public class TestDAO {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        
        String adminId = "admin";
        int adminProgramId = 1; 
        String adminName = "PaperVault Admin";
        String adminPassword = "adminpass";

        System.out.println("Attempting to set up Admin user...");
        if (dao.signUpStudent(adminId, adminName, adminProgramId, adminPassword)) {
            System.out.println("Admin user setup complete.");
        } else {
            System.out.println("Admin user already exists or failed to create (check console for error).");
        }
        
        Student authenticatedStudent = dao.authenticateStudent(adminId, adminPassword);

        if (authenticatedStudent != null) {
            System.out.println("\nAuthentication SUCCESSFUL!");
            System.out.println("Welcome, " + authenticatedStudent.getName());
        } else {
            System.out.println("\nAuthentication FAILED.");
        }
        
        authenticatedStudent = dao.authenticateStudent(adminId, "wrongpassword");
        if (authenticatedStudent == null) {
            System.out.println("Failed login test successful.");
        }
    }
}