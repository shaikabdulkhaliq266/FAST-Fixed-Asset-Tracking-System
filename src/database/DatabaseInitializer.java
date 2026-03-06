package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseInitializer.java
 * Responsible for initializing the database schema at application startup.
 * Creates all required tables if they do not already exist.
 * Safe to call multiple times — uses CREATE TABLE IF NOT EXISTS.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    // Prevent instantiation
    private DatabaseInitializer() {}

    /**
     * Creates Asset, Employee, and Assignment tables in the SQLite database.
     * Matches all fields defined in the model classes.
     */
    public static void initialize() {

        // ─────────────────────────────────────────────
        // Asset Table
        // Stores all fixed assets with serial number,
        // purchase price, category and current status
        // ─────────────────────────────────────────────
        String createAssetTable = """
                CREATE TABLE IF NOT EXISTS Asset (
                    assetId       INTEGER PRIMARY KEY AUTOINCREMENT,
                    assetName     TEXT    NOT NULL,
                    serialNumber  TEXT    NOT NULL UNIQUE,
                    category      TEXT    NOT NULL,
                    purchasePrice REAL    NOT NULL DEFAULT 0.0,
                    status        TEXT    NOT NULL DEFAULT 'Available'
                );
                """;

        // ─────────────────────────────────────────────
        // Employee Table
        // Stores all employees who can be assigned assets
        // ─────────────────────────────────────────────
        String createEmployeeTable = """
                CREATE TABLE IF NOT EXISTS Employee (
                    employeeId    INTEGER PRIMARY KEY AUTOINCREMENT,
                    employeeName  TEXT    NOT NULL,
                    department    TEXT    NOT NULL,
                    email         TEXT
                );
                """;

        // ─────────────────────────────────────────────
        // Assignment Table
        // Tracks every check-out and check-in of assets
        // returnDate is NULL until the asset is returned
        // ─────────────────────────────────────────────
        String createAssignmentTable = """
                CREATE TABLE IF NOT EXISTS Assignment (
                    assignmentId  INTEGER PRIMARY KEY AUTOINCREMENT,
                    assetId       INTEGER NOT NULL,
                    employeeId    INTEGER NOT NULL,
                    assignedDate  TEXT    NOT NULL,
                    returnDate    TEXT,
                    status        TEXT    NOT NULL DEFAULT 'Assigned',
                    FOREIGN KEY (assetId)    REFERENCES Asset(assetId),
                    FOREIGN KEY (employeeId) REFERENCES Employee(employeeId)
                );
                """;

        try (Connection conn = Dataconnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createAssetTable);
            LOGGER.log(Level.INFO, "Asset table ready.");

            stmt.execute(createEmployeeTable);
            LOGGER.log(Level.INFO, "Employee table ready.");

            stmt.execute(createAssignmentTable);
            LOGGER.log(Level.INFO, "Assignment table ready.");

            LOGGER.log(Level.INFO, "All database tables initialized successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database tables: {0}", e.getMessage());
        }
    }
}