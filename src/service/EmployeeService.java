package service;

import database.Dataconnection;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EmployeeService.java
 * Handles all database operations related to Employees.
 * Includes full CRUD and search functionality.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class EmployeeService {

    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());

    // ─────────────────────────────────────────────
    // ADD employee
    // ─────────────────────────────────────────────
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO Employee (employeeName, department, email) VALUES (?, ?, ?)";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getDepartment());
            stmt.setString(3, employee.getEmail());
            stmt.executeUpdate();

            LOGGER.log(Level.INFO, "Employee added: {0}", employee.getEmployeeName());
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add employee: {0}", e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // GET all employees
    // ─────────────────────────────────────────────
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee";

        try (Connection conn = Dataconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("employeeId"),
                        rs.getString("employeeName"),
                        rs.getString("department"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve employees: {0}", e.getMessage());
        }
        return employees;
    }

    // ─────────────────────────────────────────────
    // GET employees by department
    // ─────────────────────────────────────────────
    public List<Employee> getEmployeesByDepartment(String department) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE department = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("employeeId"),
                        rs.getString("employeeName"),
                        rs.getString("department"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve employees by department: {0}", e.getMessage());
        }
        return employees;
    }

    // ─────────────────────────────────────────────
    // SEARCH employees by name or department
    // ─────────────────────────────────────────────
    public List<Employee> searchEmployees(String keyword) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE employeeName LIKE ? OR department LIKE ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("employeeId"),
                        rs.getString("employeeName"),
                        rs.getString("department"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search employees: {0}", e.getMessage());
        }
        return employees;
    }

    // ─────────────────────────────────────────────
    // UPDATE employee
    // ─────────────────────────────────────────────
    public void updateEmployee(int employeeId, String newName,
                               String newDepartment, String newEmail) {
        String sql = "UPDATE Employee SET employeeName = ?, department = ?, " +
                "email = ? WHERE employeeId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, newDepartment);
            stmt.setString(3, newEmail);
            stmt.setInt(4, employeeId);
            stmt.executeUpdate();

            LOGGER.log(Level.INFO, "Employee updated. ID: {0}", employeeId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update employee: {0}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // DELETE employee
    // ─────────────────────────────────────────────
    public void deleteEmployee(int employeeId) {
        String sql = "DELETE FROM Employee WHERE employeeId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Employee deleted. ID: {0}", employeeId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete employee: {0}", e.getMessage());
        }
    }
}