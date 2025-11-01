package com.papervault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class StudentDAO {

    /**
     * Attempts to authenticate a student using their ID and plaintext password.
     * @param studentId The student's login ID (PK).
     * @param password The plaintext password entered by the user.
     * @return The Student object if authentication succeeds, or null otherwise.
     */
    public Student authenticateStudent(String studentId, String password) {
        String sql = "SELECT student_id, program_id, name, password_hash FROM students WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    // Core Security Check: Verify the provided password against the stored hash
                    if (BCrypt.checkpw(password, storedHash)) {
                        // Password matches! Create and return the Student object.
                        return new Student(
                            rs.getString("student_id"),
                            rs.getInt("program_id"),
                            rs.getString("name")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        // Authentication failed (ID not found or password mismatch)
        return null;
    }

    // Placeholder for adding a student (Admin function)
    public boolean addStudent(Student student, String plaintextPassword) {
        // Hashing the password before storing it
        String hashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
        String sql = "INSERT INTO students (student_id, program_id, name, password_hash) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getStudentId());
            stmt.setInt(2, student.getProgramId());
            stmt.setString(3, student.getName());
            stmt.setString(4, hashedPassword);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}