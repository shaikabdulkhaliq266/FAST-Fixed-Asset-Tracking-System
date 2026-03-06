package model;

/**
 * Assignment.java
 * Represents an asset assignment to an employee in the Asset Tracking System.
 * Tracks both check-out (assigned) and check-in (returned) dates and status.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class Assignment {

    // ─────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────
    private int    assignmentId;
    private int    assetId;
    private int    employeeId;
    private String assignedDate;   // CHECK-OUT date
    private String returnDate;     // CHECK-IN date (null if still assigned)
    private String status;         // "Assigned" or "Returned"

    // ─────────────────────────────────────────────
    // Constructor — new assignment (no return date yet)
    // ─────────────────────────────────────────────
    public Assignment(int assignmentId, int assetId,
                      int employeeId, String assignedDate) {

        if (assignedDate == null || assignedDate.trim().isEmpty())
            throw new IllegalArgumentException("Assigned date cannot be empty.");

        this.assignmentId = assignmentId;
        this.assetId      = assetId;
        this.employeeId   = employeeId;
        this.assignedDate = assignedDate.trim();
        this.returnDate   = null;
        this.status       = "Assigned";
    }

    // ─────────────────────────────────────────────
    // Full Constructor — used when loading from DB
    // ─────────────────────────────────────────────
    public Assignment(int assignmentId, int assetId, int employeeId,
                      String assignedDate, String returnDate, String status) {

        this(assignmentId, assetId, employeeId, assignedDate);
        this.returnDate = returnDate;
        this.status     = status;
    }

    // ─────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────
    public int    getAssignmentId()  { return assignmentId; }
    public int    getAssetId()       { return assetId; }
    public int    getEmployeeId()    { return employeeId; }
    public String getAssignedDate()  { return assignedDate; }
    public String getReturnDate()    { return returnDate != null ? returnDate : "Still Assigned"; }
    public String getStatus()        { return status; }

    // ─────────────────────────────────────────────
    // Setters
    // ─────────────────────────────────────────────
    public void setAssignmentId(int assignmentId)    { this.assignmentId = assignmentId; }
    public void setAssetId(int assetId)              { this.assetId = assetId; }
    public void setEmployeeId(int employeeId)        { this.employeeId = employeeId; }
    public void setAssignedDate(String assignedDate) { this.assignedDate = assignedDate; }
    public void setReturnDate(String returnDate)     { this.returnDate = returnDate; }
    public void setStatus(String status)             { this.status = status; }

    // ─────────────────────────────────────────────
    // toString
    // ─────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
                "Assignment ID: %d | Asset ID: %d | Employee ID: %d | Assigned: %s | Returned: %s | Status: %s",
                assignmentId, assetId, employeeId, assignedDate,
                returnDate != null ? returnDate : "Still Assigned",
                status
        );
    }
}