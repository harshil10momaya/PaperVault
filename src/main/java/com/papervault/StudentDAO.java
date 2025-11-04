package com.papervault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class StudentDAO {

    /**
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
                    
                    if (BCrypt.checkpw(password, storedHash)) {
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
        return null;
    }

    /**
     * @param studentId
     * @param name
     * @param programId 
     * @param plaintextPassword 
     * @return
     */
    public boolean signUpStudent(String studentId, String name, int programId, String plaintextPassword) {
        String hashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
        String sql = "INSERT INTO students (student_id, program_id, name, password_hash) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setInt(2, programId);
            stmt.setString(3, name);
            stmt.setString(4, hashedPassword);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error signing up student: " + e.getMessage());
            if (e.getSQLState().equals("23505")) { 
                System.err.println("Error: Student ID already exists.");
            }
            return false;
        }
    }
}