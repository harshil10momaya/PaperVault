package com.papervault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    /**
     * Fetches all courses associated with a specific program ID.
     * This supports filtering the dashboard for the student's program.
     */
    public List<Course> getCoursesByProgram(int programId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, program_id, course_code, course_title, semester FROM courses WHERE program_id = ?";

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
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return courses;
    }
}