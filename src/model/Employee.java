package model;

/**
 * Employee.java
 * Represents an employee in the Fixed Asset Tracking System.
 * Employees can be assigned assets and tracked by department.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class Employee {

    // ─────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────
    private int    employeeId;
    private String employeeName;
    private String department;
    private String email;

    // ─────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────
    public Employee(int employeeId, String employeeName,
                    String department, String email) {

        if (employeeName == null || employeeName.trim().isEmpty())
            throw new IllegalArgumentException("Employee name cannot be empty.");
        if (department == null || department.trim().isEmpty())
            throw new IllegalArgumentException("Department cannot be empty.");

        this.employeeId   = employeeId;
        this.employeeName = employeeName.trim();
        this.department   = department.trim();
        this.email        = (email != null) ? email.trim() : "";
    }

    // ─────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────
    public int    getEmployeeId()    { return employeeId; }
    public String getEmployeeName()  { return employeeName; }
    public String getDepartment()    { return department; }
    public String getEmail()         { return email; }

    // ─────────────────────────────────────────────
    // Setters with validation
    // ─────────────────────────────────────────────
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        if (employeeName == null || employeeName.trim().isEmpty())
            throw new IllegalArgumentException("Employee name cannot be empty.");
        this.employeeName = employeeName.trim();
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty())
            throw new IllegalArgumentException("Department cannot be empty.");
        this.department = department.trim();
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    // ─────────────────────────────────────────────
    // toString
    // ─────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
                "Employee ID: %d | Name: %-20s | Department: %-15s | Email: %s",
                employeeId, employeeName, department, email
        );
    }
}