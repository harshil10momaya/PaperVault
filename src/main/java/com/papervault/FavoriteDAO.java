package com.papervault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {
    public boolean addFavorite(String studentId, int paperId) {
        String sql = "INSERT INTO favorites (student_id, paper_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setInt(2, paperId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            if (!e.getSQLState().equals("23505")) {
                System.err.println("Error adding favorite: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean removeFavorite(String studentId, int paperId) {
        String sql = "DELETE FROM favorites WHERE student_id = ? AND paper_id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setInt(2, paperId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing favorite: " + e.getMessage());
            return false;
        }
    }

    public List<Paper> getFavoritesByStudent(String studentId) {
        List<Paper> papers = new ArrayList<>();
        
        String sql = "SELECT p.* FROM papers p JOIN favorites f ON p.paper_id = f.paper_id WHERE f.student_id = ? ORDER BY p.upload_date DESC";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
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
            System.err.println("Error retrieving favorites: " + e.getMessage());
        }
        return papers;
    }
}