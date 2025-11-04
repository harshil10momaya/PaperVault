package com.papervault;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private static DatabaseConnector instance;
    private Connection connection;
    private String url;
    private String user;
    private String password;

    private DatabaseConnector() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            
            InputStream fileInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");

            if (fileInput != null) {
                props.load(fileInput);
            } else {
                System.out.println("Warning: db.properties not found in classpath. Check location.");
                
                props.setProperty("db.url", "jdbc:postgresql://localhost:5432/papervault_db");
                props.setProperty("db.user", "postgres"); 
                props.setProperty("db.password", "Harshil@10");
            }
            
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

            Class.forName("org.postgresql.Driver"); 

        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
        }
    }

    /**
     * @return The single DatabaseConnector instance.
     */
    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    /**
     * Establishes and returns a new database connection.
     * @return A database Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Attempting to connect to database...");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established successfully.");
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnector.getInstance().getConnection();
            if (conn != null) {
                System.out.println("Test successful! Connection is ready.");
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT program_name FROM programs LIMIT 1");
                if (rs.next()) {
                    System.out.println("First program name: " + rs.getString("program_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Test failed: Could not establish connection or query database.");
            e.printStackTrace();
        } finally {
            DatabaseConnector.getInstance().closeConnection();
        }
    }
}