package com.papervault;

public class Program {
    private int programId;
    private String programCode;
    private String programName;

    public Program(int programId, String programCode, String programName) {
        this.programId = programId;
        this.programCode = programName.split(" - ")[0].trim();
        this.programName = programName;
    }

    public int getProgramId() { return programId; }
    public String getProgramCode() { return programCode; }
    public String getProgramName() { return programName; }
    
    @Override
    public String toString() {
        return programName;
    }
}