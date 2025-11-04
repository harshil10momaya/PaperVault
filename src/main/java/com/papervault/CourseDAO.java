package com.papervault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getCoursesByProgramAndSemester(int programId, int semester) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, program_id, course_code, course_title, semester FROM courses WHERE program_id = ? AND semester = ? ORDER BY course_code";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, programId);
            stmt.setInt(2, semester);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getInt("program_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("semester")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses by program and semester: " + e.getMessage());
        }
        return courses;
    }
    
    public List<Course> getAllCoursesByProgram(int programId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, program_id, course_code, course_title, semester FROM courses WHERE program_id = ? ORDER BY course_code";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, programId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getInt("program_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("semester")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all courses by program: " + e.getMessage());
        }
        return courses;
    }
    
    public Course getCourseById(int courseId) {
        String sql = "SELECT course_id, program_id, course_code, course_title, semester FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("course_id"),
                        rs.getInt("program_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("semester")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching single course: " + e.getMessage());
        }
        return null;
    }
}