package com.papervault;

public class TestDAO {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        
        // 1. Setup Data: Program ID 1 is for TCS
        String adminId = "ADMIN001";
        int adminProgramId = 1; 
        String adminName = "PaperVault Admin";
        String adminPassword = "password123";

        // We use signUpStudent now, which takes name and programId
        System.out.println("Attempting to set up Admin user...");
        if (dao.signUpStudent(adminId, adminName, adminProgramId, adminPassword)) {
            System.out.println("Admin user setup complete.");
        } else {
            // Note: This often runs if the user already exists (which is fine)
            System.out.println("Admin user already exists or failed to create (check console for error).");
        }
        
        // 2. Test Authentication (Should succeed)
        Student authenticatedStudent = dao.authenticateStudent(adminId, adminPassword);

        if (authenticatedStudent != null) {
            System.out.println("\n✅ Authentication SUCCESSFUL!");
            System.out.println("Welcome, " + authenticatedStudent.getName());
        } else {
            System.out.println("\n❌ Authentication FAILED.");
        }
        
        // 3. Test Authentication (Should fail)
        authenticatedStudent = dao.authenticateStudent(adminId, "wrongpassword");
        if (authenticatedStudent == null) {
            System.out.println("✅ Failed login test successful.");
        }
    }
}