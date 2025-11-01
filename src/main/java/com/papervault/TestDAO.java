package com.papervault;

public class TestDAO {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        
        // 1. Setup Data: Assuming Program ID 1 exists (from your inserted programs)
        Student admin = new Student("ADMIN001", 1, "PaperVault Admin");
        String adminPassword = "password123";

        // 2. Add the student (Hashed password goes into DB)
        if (dao.addStudent(admin, adminPassword)) {
            System.out.println("Admin user setup complete.");
        } else {
            System.out.println("Admin user already exists or failed to create.");
        }
        
        // 3. Test Authentication (Should succeed)
        Student authenticatedStudent = dao.authenticateStudent("ADMIN001", adminPassword);

        if (authenticatedStudent != null) {
            System.out.println("\n✅ Authentication SUCCESSFUL!");
            System.out.println("Welcome, " + authenticatedStudent.getName());
        } else {
            System.out.println("\n❌ Authentication FAILED.");
        }
        
        // 4. Test Authentication (Should fail)
        authenticatedStudent = dao.authenticateStudent("ADMIN001", "wrongpassword");
        if (authenticatedStudent == null) {
            System.out.println("✅ Failed login test successful.");
        }
    }
}