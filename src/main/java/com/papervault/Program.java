package com.papervault;

/**
 * Model class for the 'programs' table.
 */
public class Program {
    private int programId;
    private String programCode;
    private String programName;

    public Program(int programId, String programCode, String programName) {
        this.programId = programId;
        // Extract the code (e.g., 23XT) from the full name
        this.programCode = programName.split(" - ")[0].trim();
        this.programName = programName;
    }

    // --- Getters ---
    public int getProgramId() { return programId; }
    public String getProgramCode() { return programCode; }
    public String getProgramName() { return programName; }
    
    @Override
    public String toString() {
        return programName;
    }
}