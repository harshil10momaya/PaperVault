package com.papervault;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    // Singleton instance
    private static DatabaseConnector instance;
    private Connection connection;
    private String url;
    private String user;
    private String password;

    /**
     * Private constructor to prevent external instantiation.
     * Loads properties and registers the JDBC driver.
     */
    private DatabaseConnector() {
        // Load database configuration from db.properties file
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            
            InputStream fileInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");

            if (fileInput != null) {
                props.load(fileInput);
            } else {
                // Fallback for local testing (may need adjustment based on IDE setup)
                System.out.println("Warning: db.properties not found in classpath. Check location.");
                
                // Use default values for testing if file fails to load
                props.setProperty("db.url", "jdbc:postgresql://localhost:5432/papervault_db");
                props.setProperty("db.user", "postgres"); 
                props.setProperty("db.password", "Harshil@10");
            }
            
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

            // Optional: Load the PostgreSQL JDBC driver explicitly (not strictly needed since Java 6)
            Class.forName("org.postgresql.Driver"); 

        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
        }
    }

    /**
     * Gets the singleton instance of the DatabaseConnector.
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
        // Only create a new connection if the existing one is null or closed
        if (connection == null || connection.isClosed()) {
            System.out.println("Attempting to connect to database...");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection established successfully.");
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
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

    // Simple test method for verification (optional)
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnector.getInstance().getConnection();
            if (conn != null) {
                System.out.println("Test successful! Connection is ready.");
                // Execute a simple query to verify
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