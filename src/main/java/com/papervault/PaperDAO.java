package com.papervault;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaperDAO {
    
    public boolean insertPaper(int courseId, int academicYear, String examType, String filePath) {
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

    public List<Paper> getPapersByCriteria(int courseId, Integer year, String examType, String searchTerm) {
        List<Paper> papers = new ArrayList<>();
        String sql = "SELECT p.*, c.course_code, c.course_title FROM papers p JOIN courses c ON p.course_id = c.course_id WHERE p.course_id = ?";
        
        int currentParam = 1;

        if (year != null) {
            sql += " AND p.academic_year = ?";
        }
        
        if (examType != null && !examType.isEmpty()) {
            sql += " AND p.exam_type = ?";
        }
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql += " AND (c.course_code ILIKE ? OR c.course_title ILIKE ?)";
        }
        
        sql += " ORDER BY p.academic_year DESC, p.upload_date DESC";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(currentParam++, courseId);
            
            if (year != null) {
                stmt.setInt(currentParam++, year);
            }
            if (examType != null && !examType.isEmpty()) {
                stmt.setString(currentParam++, examType);
            }
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                stmt.setString(currentParam++, "%" + searchTerm + "%"); 
                stmt.setString(currentParam++, "%" + searchTerm + "%");
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
            System.err.println("Error retrieving papers with criteria: " + e.getMessage());
        }
        return papers;
    }
    
    public boolean isFavorited(String studentId, int paperId) {
        String sql = "SELECT 1 FROM favorites WHERE student_id = ? AND paper_id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setInt(2, paperId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            System.err.println("Error checking favorite status: " + e.getMessage());
            return false;
        }
    }
}