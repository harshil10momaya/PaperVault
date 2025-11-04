title: "PaperVault: Previous Year Paper Management System"

authors:
  - "23PT11 – Harshil Bhavik Momaya"
  - "23PT28 – Rohit B K"

project_goal: >
  PaperVault provides students with a secure, course-wise, and semester-specific
  platform to easily access and manage previous year examination papers (PDFs).
  The system is designed to simplify access to study materials while maintaining
  proper organization and security.

features:
  core:
    - "Secure student sign-up and login using jBCrypt for password hashing."
    - "Multi-step navigation: students select their program (TCS, DS, etc.) and semester (1–8) to view relevant subjects."
    - "Dynamic course and subject mapping based on selected program and semester."
    - "Separate admin/faculty portal for uploading papers with metadata (Course, Year, Exam Type)."
    - "View previous year papers directly from the dashboard using the system’s default PDF reader."
    - "Search and filter options based on Subject, Academic Year, and Exam Type (CA1, CA2, SEM)."
  enhanced:
    - "Option to download and save paper files locally using a file chooser."
    - "Bookmark system to mark frequently used papers, available under the 'My Bookmarks' tab."
    - "Clean and simple tab-based user interface with custom CSS styling."

how_to_run:
  prerequisites:
    - "Java Development Kit (JDK 17 or higher)"
    - "Maven installed and configured"
    - "PostgreSQL server running (default port 5432)"
    - "JavaFX dependencies (handled by Maven)"
  setup_steps:
    - "Create a PostgreSQL database named 'papervault_db'."
    - "Update database credentials in 'src/main/resources/db.properties'. Example:"
    - db_properties_example: |
        db.url=jdbc:postgresql://localhost:5432/papervault_db
        db.username=postgres
        db.password=your_password
    - "Run SQL scripts to create required tables (students, papers, favorites, etc.) and insert initial program/course data."
    - "Place the paper PDF files in the directory path used in your DML scripts, for example: C:/Users/LENOVO/Desktop/PaperVault/papers/"
    - "Open the terminal in the project root directory and run: mvn clean javafx:run"

default_credentials:
  - role: "Admin/Faculty"
    username: "admin"
    password: "adminpass"
    description: "Access to the paper upload portal"
  - role: "Student (TCS)"
    username: "ADMIN001"
    password: "password123"
    description: "Access to the student dashboard"

future_enhancements:
  - "Add a student feedback and paper rating system."
  - "Maintain a recently viewed papers list for quick access."
  - "Switch from absolute to relative file paths for easier deployment."
  - "Add advanced search filters based on metadata such as BTL levels or Course Outcomes (CO)."
  - "Move admin credentials and roles into the database for better scalability and security."

tech_stack:
  language: "Java (JDK 17+)"
  framework: "JavaFX (FXML-based)"
  database: "PostgreSQL"
  build_tool: "Maven"
  authentication: "jBCrypt"
  styling: "Custom CSS"
