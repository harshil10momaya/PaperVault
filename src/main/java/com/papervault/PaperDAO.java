package com.papervault;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaperDAO {
    
    // --- Admin Function: Insert New Paper Metadata ---
    /**
     * Inserts metadata for a new paper into the database.
     * @param courseId The ID of the associated course.
     * @param academicYear The year the exam was held.
     * @param examType The type of exam ('CA1', 'CA2', 'SEM').
     * @param filePath The local file path where the PDF is stored.
     * @return true if insertion was successful, false otherwise.
     */
    public boolean insertPaper(int courseId, int academicYear, String examType, String filePath) {
        // Note: upload_date is handled automatically by the DEFAULT NOW() in the table
        String sql = "INSERT INTO papers (course_id, academic_year, exam_type, file_path) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, academicYear);
            stmt.setString(3, examType);
            stmt.setString(4, filePath);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting paper metadata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Student Function: Retrieve Papers for Display ---
    /**
     * Retrieves papers based on a given course ID, year, and search term.
     * Used for the main Paper Viewing & Access feature (Core Feature).
     */
    public List<Paper> getPapersByCriteria(int courseId, Integer year, String searchTerm) {
        List<Paper> papers = new ArrayList<>();
        // Base query to fetch paper metadata along with Course Code/Title for display
        String sql = "SELECT p.*, c.course_code, c.course_title FROM papers p JOIN courses c ON p.course_id = c.course_id WHERE p.course_id = ?";
        
        // Add optional filtering by year
        if (year != null) {
            sql += " AND p.academic_year = " + year;
        }
        
        // Add optional filtering by search term (Subject Name or Code)
        // Note: For MVP, we search by Course Code/Title.
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql += " AND (c.course_code ILIKE ? OR c.course_title ILIKE ?)"; // ILIKE for case-insensitive search in Postgres
        }

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            int paramIndex = 2;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Set parameters for the ILIKE clauses
                stmt.setString(paramIndex++, "%" + searchTerm + "%"); 
                stmt.setString(paramIndex++, "%" + searchTerm + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    papers.add(new Paper(
                        rs.getInt("paper_id"),
                        rs.getInt("course_id"),
                        rs.getInt("academic_year"),
                        rs.getString("exam_type"),
                        rs.getString("file_path"),
                        rs.getTimestamp("upload_date").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving papers: " + e.getMessage());
        }
        return papers;
    }
}