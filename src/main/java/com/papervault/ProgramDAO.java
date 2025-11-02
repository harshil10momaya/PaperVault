package com.papervault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgramDAO {
    
    /**
     * Fetches all available programs from the database.
     */
    public List<Program> getAllPrograms() {
        List<Program> programs = new ArrayList<>();
        String sql = "SELECT program_id, program_name FROM programs ORDER BY program_id";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                programs.add(new Program(
                    rs.getInt("program_id"),
                    null,
                    rs.getString("program_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching programs: " + e.getMessage());
        }
        return programs;
    }
}