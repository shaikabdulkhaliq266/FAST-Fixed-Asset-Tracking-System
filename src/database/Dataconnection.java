package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dataconnection.java
 * Manages SQLite database connectivity for the Asset Tracking System.
 * Uses singleton-style static access — one connection per call.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class Dataconnection {

    // ─────────────────────────────────────────────
    // Constants
    // ─────────────────────────────────────────────
    private static final Logger LOGGER   = Logger.getLogger(Dataconnection.class.getName());
    private static final String JDBC_URL = "jdbc:sqlite:asset_tracking.db";

    // ─────────────────────────────────────────────
    // Load SQLite JDBC driver once at startup
    // ─────────────────────────────────────────────
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            LOGGER.log(Level.INFO, "SQLite JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQLite JDBC Driver not found! "
                    + "Make sure sqlite-jdbc jar is in your classpath.", e);
        }
    }

    // ─────────────────────────────────────────────
    // Private constructor — prevent instantiation
    // ─────────────────────────────────────────────
    private Dataconnection() {}

    // ─────────────────────────────────────────────
    // Get a fresh database connection
    // ─────────────────────────────────────────────
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL);
            LOGGER.log(Level.INFO, "Database connection established. URL: {0}", JDBC_URL);
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database: {0}", e.getMessage());
            throw e;
        }
    }

    // ─────────────────────────────────────────────
    // Test if database is reachable — used at startup
    // ─────────────────────────────────────────────
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.log(Level.INFO, "Database connection test: SUCCESS");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test: FAILED - {0}", e.getMessage());
        }
        return false;
    }
}