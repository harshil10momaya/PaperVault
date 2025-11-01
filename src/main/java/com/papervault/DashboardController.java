package com.papervault;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the main student dashboard view.
 */
public class DashboardController {

    @FXML private Label welcomeLabel;

    public void initialize() {
        // You would typically use a method like setLoggedInUser(Student) here 
        // to dynamically set the welcome message and load relevant data.
        System.out.println("Dashboard initialized.");
    }

    /**
     * Optional method to pass the logged-in user data from the LoginController.
     */
    public void setLoggedInUser(Student student) {
        welcomeLabel.setText("Welcome, " + student.getName() + " (" + student.getStudentId() + ")!");
        // Future step: Use student.getProgramId() to filter papers.
    }
}