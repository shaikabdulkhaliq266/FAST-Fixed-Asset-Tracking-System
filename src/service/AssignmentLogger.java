package service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AssignmentLogger.java
 * Writes every CHECK-OUT and CHECK-IN event to assignment_log.txt permanently.
 * File is created automatically in your project root folder.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class AssignmentLogger {

    private static final String LOG_FILE = "assignment_log.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Called when asset is assigned to employee
    public static void logCheckOut(int assetId, int employeeId) {
        writeLog("Asset: " + assetId, "CHECK-OUT", "Employee ID: " + employeeId);
    }

    // Called when asset is returned
    public static void logCheckIn(int assetId, int assignmentId) {
        writeLog("Asset: " + assetId, "CHECK-IN ", "Assignment ID: " + assignmentId);
    }

    // Appends one line to assignment_log.txt — never overwrites
    private static void writeLog(String asset, String action, String detail) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry  = String.format("%s - %s - Action: %s - %s",
                timestamp, asset, action, detail);
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logEntry);
            System.out.println("[LOG] " + logEntry);
        } catch (IOException e) {
            System.err.println("[LOG ERROR] Could not write to assignment_log.txt: "
                    + e.getMessage());
        }
    }

    // Print entire log to console
    public static void printLog() {
        java.io.File file = new java.io.File(LOG_FILE);
        if (!file.exists()) {
            System.out.println("No log file found yet.");
            return;
        }
        System.out.println("\n========== ASSIGNMENT LOG ==========");
        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            System.err.println("Could not read log: " + e.getMessage());
        }
        System.out.println("=====================================\n");
    }
}