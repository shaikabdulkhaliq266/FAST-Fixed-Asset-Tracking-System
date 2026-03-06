package service;

import database.Dataconnection;
import model.Assignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AssignmentService.java
 * Handles all database operations related to Assignments.
 * Checks asset availability before assigning.
 * Automatically logs every CHECK-OUT and CHECK-IN to assignment_log.txt.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class AssignmentService {

    private static final Logger LOGGER = Logger.getLogger(AssignmentService.class.getName());

    // ─────────────────────────────────────────────
    // ASSIGN asset to employee
    // Checks availability first — spec requirement
    // ─────────────────────────────────────────────
    public boolean assignAsset(Assignment assignment) {

        // Integrity check — asset must be Available before assigning
        if (!isAssetAvailable(assignment.getAssetId())) {
            System.out.println("[ERROR] Asset is not available for assignment.");
            return false;
        }

        String sql = "INSERT INTO Assignment (assetId, employeeId, assignedDate, status) " +
                "VALUES (?, ?, ?, 'Assigned')";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assignment.getAssetId());
            stmt.setInt(2, assignment.getEmployeeId());
            stmt.setString(3, assignment.getAssignedDate());
            stmt.executeUpdate();

            // Update asset status to Assigned
            updateAssetStatus(conn, assignment.getAssetId(), "Assigned");

            // Log CHECK-OUT to assignment_log.txt
            AssignmentLogger.logCheckOut(assignment.getAssetId(), assignment.getEmployeeId());

            LOGGER.log(Level.INFO, "Asset {0} assigned to Employee {1}",
                    new Object[]{assignment.getAssetId(), assignment.getEmployeeId()});
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to assign asset: {0}", e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // GET all assignments
    // ─────────────────────────────────────────────
    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM Assignment";

        try (Connection conn = Dataconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                assignments.add(new Assignment(
                        rs.getInt("assignmentId"),
                        rs.getInt("assetId"),
                        rs.getInt("employeeId"),
                        rs.getString("assignedDate"),
                        rs.getString("returnDate"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve assignments: {0}", e.getMessage());
        }
        return assignments;
    }

    // ─────────────────────────────────────────────
    // GET only active assignments (not returned yet)
    // ─────────────────────────────────────────────
    public List<Assignment> getActiveAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM Assignment WHERE status = 'Assigned'";

        try (Connection conn = Dataconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                assignments.add(new Assignment(
                        rs.getInt("assignmentId"),
                        rs.getInt("assetId"),
                        rs.getInt("employeeId"),
                        rs.getString("assignedDate"),
                        rs.getString("returnDate"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve active assignments: {0}", e.getMessage());
        }
        return assignments;
    }

    // ─────────────────────────────────────────────
    // RETURN asset — updates status, logs CHECK-IN
    // ─────────────────────────────────────────────
    public boolean returnAsset(int assignmentId, int assetId) {
        String sql = "UPDATE Assignment SET status = 'Returned', " +
                "returnDate = datetime('now') WHERE assignmentId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assignmentId);
            stmt.executeUpdate();

            // Mark asset as Available again
            updateAssetStatus(conn, assetId, "Available");

            // Log CHECK-IN to assignment_log.txt
            AssignmentLogger.logCheckIn(assetId, assignmentId);

            LOGGER.log(Level.INFO, "Asset returned. Assignment ID: {0}", assignmentId);
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to return asset: {0}", e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // CHECK if asset is Available before assigning
    // Prevents assigning to two people simultaneously
    // ─────────────────────────────────────────────
    public boolean isAssetAvailable(int assetId) {
        String sql = "SELECT status FROM Asset WHERE assetId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assetId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status").equalsIgnoreCase("Available");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check asset availability: {0}", e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // VIEW assignment log in console
    // ─────────────────────────────────────────────
    public void viewLog() {
        AssignmentLogger.printLog();
    }

    // ─────────────────────────────────────────────
    // Private helper — update asset status
    // ─────────────────────────────────────────────
    private void updateAssetStatus(Connection conn, int assetId, String status) throws SQLException {
        String sql = "UPDATE Asset SET status = ? WHERE assetId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, assetId);
            stmt.executeUpdate();
        }
    }
}