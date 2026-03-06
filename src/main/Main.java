package main;

import database.DatabaseInitializer;
import model.Asset;
import model.Assignment;
import model.Employee;
import service.AssetService;
import service.AssignmentService;
import service.AssignmentLogger;
import service.EmployeeService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Main.java
 * Entry point for the Fixed Asset Tracking System.
 * Console UI with ANSI colors, full CRUD, search, and report export.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class Main {

    // ─────────────────────────────────────────────
    // Services & Scanner
    // ─────────────────────────────────────────────
    private static final Scanner           scanner           = new Scanner(System.in);
    private static final AssetService      assetService      = new AssetService();
    private static final EmployeeService   employeeService   = new EmployeeService();
    private static final AssignmentService assignmentService = new AssignmentService();

    // ─────────────────────────────────────────────
    // ANSI Colors
    // ─────────────────────────────────────────────
    private static final String RESET  = "\u001B[0m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String BOLD   = "\u001B[1m";
    private static final String BLUE   = "\u001B[34m";

    // ─────────────────────────────────────────────
    // Main Entry Point
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> manageAssets();
                case 2 -> manageEmployees();
                case 3 -> manageAssignments();
                case 4 -> exportStatusReport();
                case 5 -> {
                    System.out.println(RED + BOLD);
                    System.out.println("  +--------------------------------------+");
                    System.out.println("  |    Goodbye! See you next time...     |");
                    System.out.println("  +--------------------------------------+" + RESET);
                    running = false;
                }
                default -> printError("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    // ─────────────────────────────────────────────
    // Startup Banner
    // ─────────────────────────────────────────────
    private static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║    FIXED ASSET TRACKING SYSTEM       ║");
        System.out.println("  ║    Muffakham Jah College of Engg.    ║");
        System.out.println("  ║    MJ-IRP | Academic Year 2025-26    ║");
        System.out.println("  ╚══════════════════════════════════════╝" + RESET);
    }

    // ─────────────────────────────────────────────
    // Main Menu
    // ─────────────────────────────────────────────
    private static void printMainMenu() {
        System.out.println();
        System.out.println(CYAN + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(CYAN + BOLD + "  |      FIXED ASSET TRACKING SYSTEM     |" + RESET);
        System.out.println(CYAN + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(CYAN + "  |  " + YELLOW + "1." + RESET + "  Manage Assets                 " + CYAN + "|" + RESET);
        System.out.println(CYAN + "  |  " + YELLOW + "2." + RESET + "  Manage Employees              " + CYAN + "|" + RESET);
        System.out.println(CYAN + "  |  " + YELLOW + "3." + RESET + "  Manage Assignments            " + CYAN + "|" + RESET);
        System.out.println(CYAN + "  |  " + YELLOW + "4." + RESET + "  Export Asset Status Report    " + CYAN + "|" + RESET);
        System.out.println(CYAN + "  |  " + YELLOW + "5." + RESET + "  Exit                          " + CYAN + "|" + RESET);
        System.out.println(CYAN + BOLD + "  +--------------------------------------+" + RESET);
    }

    // ─────────────────────────────────────────────
    // ASSET MANAGEMENT
    // ─────────────────────────────────────────────
    private static void manageAssets() {
        System.out.println();
        System.out.println(GREEN + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(GREEN + BOLD + "  |          ASSET MANAGEMENT            |" + RESET);
        System.out.println(GREEN + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "1." + RESET + "  Add Asset                     " + GREEN + "|" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "2." + RESET + "  View All Assets               " + GREEN + "|" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "3." + RESET + "  Delete Asset                  " + GREEN + "|" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "4." + RESET + "  Search Asset                  " + GREEN + "|" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "5." + RESET + "  Update Asset                  " + GREEN + "|" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "6." + RESET + "  Filter by Status              " + GREEN + "|" + RESET);
        System.out.println(GREEN + "  |  " + YELLOW + "7." + RESET + "  Filter by Category            " + GREEN + "|" + RESET);
        System.out.println(GREEN + BOLD + "  +--------------------------------------+" + RESET);

        int choice = readInt("  Enter choice: ");
        switch (choice) {
            case 1 -> {
                String name   = readNonEmpty("  Asset Name     : ");
                String serial = readNonEmpty("  Serial Number  : ");
                String cat    = readNonEmpty("  Category       : ");
                double price  = readDouble("  Purchase Price : ");
                boolean added = assetService.addAsset(new Asset(0, name, serial, cat, price, "Available"));
                if (added) printSuccess("Asset added successfully.");
                else       printError("Failed — serial number may already exist.");
            }
            case 2 -> printAssets(assetService.getAllAssets(), "ALL ASSETS");
            case 3 -> {
                int id = readPositiveInt("  Enter Asset ID to delete: ");
                assetService.deleteAsset(id);
                printSuccess("Asset deleted successfully.");
            }
            case 4 -> {
                String keyword = readNonEmpty("  Search keyword: ");
                printAssets(assetService.searchAssets(keyword), "SEARCH RESULTS");
            }
            case 5 -> {
                int    id      = readPositiveInt("  Enter Asset ID to update: ");
                String newName = readNonEmpty("  New Name       : ");
                String newCat  = readNonEmpty("  New Category   : ");
                double newPrice= readDouble("  New Price      : ");
                String newStat = readStatus();
                assetService.updateAsset(id, newName, newCat, newPrice, newStat);
                printSuccess("Asset updated successfully.");
            }
            case 6 -> {
                String status = readStatus();
                printAssets(assetService.getAssetsByStatus(status), "ASSETS BY STATUS: " + status);
            }
            case 7 -> {
                String cat = readNonEmpty("  Enter Category : ");
                printAssets(assetService.getAssetsByCategory(cat), "ASSETS BY CATEGORY: " + cat);
            }
            default -> printError("Invalid choice.");
        }
    }

    // ─────────────────────────────────────────────
    // EMPLOYEE MANAGEMENT
    // ─────────────────────────────────────────────
    private static void manageEmployees() {
        System.out.println();
        System.out.println(BLUE + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(BLUE + BOLD + "  |         EMPLOYEE MANAGEMENT          |" + RESET);
        System.out.println(BLUE + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(BLUE + "  |  " + YELLOW + "1." + RESET + "  Add Employee                  " + BLUE + "|" + RESET);
        System.out.println(BLUE + "  |  " + YELLOW + "2." + RESET + "  View All Employees            " + BLUE + "|" + RESET);
        System.out.println(BLUE + "  |  " + YELLOW + "3." + RESET + "  Delete Employee               " + BLUE + "|" + RESET);
        System.out.println(BLUE + "  |  " + YELLOW + "4." + RESET + "  Search Employee               " + BLUE + "|" + RESET);
        System.out.println(BLUE + "  |  " + YELLOW + "5." + RESET + "  Update Employee               " + BLUE + "|" + RESET);
        System.out.println(BLUE + BOLD + "  +--------------------------------------+" + RESET);

        int choice = readInt("  Enter choice: ");
        switch (choice) {
            case 1 -> {
                String name  = readNonEmpty("  Employee Name : ");
                String dept  = readNonEmpty("  Department    : ");
                String email = readNonEmpty("  Email         : ");
                boolean added = employeeService.addEmployee(new Employee(0, name, dept, email));
                if (added) printSuccess("Employee added successfully.");
                else       printError("Failed to add employee.");
            }
            case 2 -> printEmployees(employeeService.getAllEmployees(), "ALL EMPLOYEES");
            case 3 -> {
                int id = readPositiveInt("  Enter Employee ID to delete: ");
                employeeService.deleteEmployee(id);
                printSuccess("Employee deleted successfully.");
            }
            case 4 -> {
                String keyword = readNonEmpty("  Search keyword: ");
                printEmployees(employeeService.searchEmployees(keyword), "SEARCH RESULTS");
            }
            case 5 -> {
                int    id      = readPositiveInt("  Enter Employee ID to update: ");
                String newName = readNonEmpty("  New Name       : ");
                String newDept = readNonEmpty("  New Department : ");
                String newEmail= readNonEmpty("  New Email      : ");
                employeeService.updateEmployee(id, newName, newDept, newEmail);
                printSuccess("Employee updated successfully.");
            }
            default -> printError("Invalid choice.");
        }
    }

    // ─────────────────────────────────────────────
    // ASSIGNMENT MANAGEMENT
    // ─────────────────────────────────────────────
    private static void manageAssignments() {
        System.out.println();
        System.out.println(YELLOW + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(YELLOW + BOLD + "  |        ASSIGNMENT MANAGEMENT         |" + RESET);
        System.out.println(YELLOW + BOLD + "  +--------------------------------------+" + RESET);
        System.out.println(YELLOW + "  |  " + CYAN + "1." + RESET + "  Assign Asset to Employee      " + YELLOW + "|" + RESET);
        System.out.println(YELLOW + "  |  " + CYAN + "2." + RESET + "  View All Assignments          " + YELLOW + "|" + RESET);
        System.out.println(YELLOW + "  |  " + CYAN + "3." + RESET + "  View Active Assignments       " + YELLOW + "|" + RESET);
        System.out.println(YELLOW + "  |  " + CYAN + "4." + RESET + "  Return Asset                  " + YELLOW + "|" + RESET);
        System.out.println(YELLOW + "  |  " + CYAN + "5." + RESET + "  View Assignment Log           " + YELLOW + "|" + RESET);
        System.out.println(YELLOW + BOLD + "  +--------------------------------------+" + RESET);

        int choice = readInt("  Enter choice: ");
        switch (choice) {
            case 1 -> {
                int assetId    = readPositiveInt("  Enter Asset ID    : ");
                int employeeId = readPositiveInt("  Enter Employee ID : ");
                boolean success = assignmentService.assignAsset(
                        new Assignment(0, assetId, employeeId, LocalDate.now().toString()));
                if (success) printSuccess("Asset assigned successfully.");
                else         printError("Failed — asset may not be available.");
            }
            case 2 -> printAssignments(assignmentService.getAllAssignments(), "ALL ASSIGNMENTS");
            case 3 -> printAssignments(assignmentService.getActiveAssignments(), "ACTIVE ASSIGNMENTS");
            case 4 -> {
                int assignmentId = readPositiveInt("  Enter Assignment ID : ");
                int assetId      = readPositiveInt("  Enter Asset ID      : ");
                boolean success  = assignmentService.returnAsset(assignmentId, assetId);
                if (success) printSuccess("Asset returned successfully.");
                else         printError("Failed to return asset.");
            }
            case 5 -> assignmentService.viewLog();
            default -> printError("Invalid choice.");
        }
    }

    // ─────────────────────────────────────────────
    // EXPORT Asset_Status_Report.txt
    // Required by spec — shows all deployed assets
    // ─────────────────────────────────────────────
    private static void exportStatusReport() {
        String filename  = "Asset_Status_Report.txt";
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<Asset>    allAssets    = assetService.getAllAssets();
        List<Employee> allEmployees = employeeService.getAllEmployees();
        List<Assignment> active     = assignmentService.getActiveAssignments();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            writer.println("=================================================");
            writer.println("       FIXED ASSET TRACKING SYSTEM");
            writer.println("       Asset Status Report");
            writer.println("       Generated: " + timestamp);
            writer.println("=================================================");
            writer.println();

            // Summary
            long available   = allAssets.stream().filter(a -> a.getStatus().equals("Available")).count();
            long assigned    = allAssets.stream().filter(a -> a.getStatus().equals("Assigned")).count();
            long maintenance = allAssets.stream().filter(a -> a.getStatus().equals("Maintenance")).count();
            long retired     = allAssets.stream().filter(a -> a.getStatus().equals("Retired")).count();

            writer.println("SUMMARY");
            writer.println("-------------------------------------------------");
            writer.println("Total Assets     : " + allAssets.size());
            writer.println("Available        : " + available);
            writer.println("Assigned         : " + assigned);
            writer.println("Maintenance      : " + maintenance);
            writer.println("Retired          : " + retired);
            writer.println();

            // Currently deployed assets
            writer.println("CURRENTLY DEPLOYED ASSETS");
            writer.println("-------------------------------------------------");
            if (active.isEmpty()) {
                writer.println("No assets currently deployed.");
            } else {
                for (Assignment a : active) {
                    String assetName = allAssets.stream()
                            .filter(ast -> ast.getAssetId() == a.getAssetId())
                            .map(Asset::getAssetName).findFirst().orElse("Unknown");
                    String empName = allEmployees.stream()
                            .filter(e -> e.getEmployeeId() == a.getEmployeeId())
                            .map(Employee::getEmployeeName).findFirst().orElse("Unknown");
                    writer.println("Asset    : " + assetName);
                    writer.println("Employee : " + empName);
                    writer.println("Since    : " + a.getAssignedDate());
                    writer.println("-------------------------------------------------");
                }
            }

            // All assets list
            writer.println();
            writer.println("FULL ASSET INVENTORY");
            writer.println("-------------------------------------------------");
            for (Asset a : allAssets) {
                writer.println(a.toString());
            }

            writer.println();
            writer.println("=================================================");
            writer.println("END OF REPORT");
            writer.println("=================================================");

            printSuccess("Report exported successfully → " + filename);

        } catch (IOException e) {
            printError("Failed to export report: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // Display Helpers
    // ─────────────────────────────────────────────
    private static void printAssets(List<Asset> assets, String title) {
        if (assets.isEmpty()) {
            printError("No assets found.");
            return;
        }
        System.out.println(GREEN + BOLD + "\n  +--------------------------------------+" + RESET);
        System.out.println(GREEN + BOLD + "  |  " + title + RESET);
        System.out.println(GREEN + BOLD + "  +--------------------------------------+" + RESET);
        assets.forEach(a -> System.out.println(CYAN + "  | " + RESET + a));
        System.out.println(GREEN + BOLD + "  +--------------------------------------+" + RESET);
    }

    private static void printEmployees(List<Employee> employees, String title) {
        if (employees.isEmpty()) {
            printError("No employees found.");
            return;
        }
        System.out.println(BLUE + BOLD + "\n  +--------------------------------------+" + RESET);
        System.out.println(BLUE + BOLD + "  |  " + title + RESET);
        System.out.println(BLUE + BOLD + "  +--------------------------------------+" + RESET);
        employees.forEach(e -> System.out.println(CYAN + "  | " + RESET + e));
        System.out.println(BLUE + BOLD + "  +--------------------------------------+" + RESET);
    }

    private static void printAssignments(List<Assignment> assignments, String title) {
        if (assignments.isEmpty()) {
            printError("No assignments found.");
            return;
        }
        System.out.println(YELLOW + BOLD + "\n  +--------------------------------------+" + RESET);
        System.out.println(YELLOW + BOLD + "  |  " + title + RESET);
        System.out.println(YELLOW + BOLD + "  +--------------------------------------+" + RESET);
        assignments.forEach(a -> System.out.println(CYAN + "  | " + RESET + a));
        System.out.println(YELLOW + BOLD + "  +--------------------------------------+" + RESET);
    }

    // ─────────────────────────────────────────────
    // Input Helpers
    // ─────────────────────────────────────────────
    private static void printSuccess(String message) {
        System.out.println(GREEN + BOLD + "\n  >> " + message + RESET);
    }

    private static void printError(String message) {
        System.out.println(RED + BOLD + "\n  !! " + message + RESET);
    }

    private static String readNonEmpty(String prompt) {
        String value = "";
        while (value.trim().isEmpty()) {
            System.out.print(YELLOW + prompt + RESET);
            value = scanner.nextLine();
            if (value.trim().isEmpty()) printError("This field cannot be empty.");
        }
        return value.trim();
    }

    private static int readPositiveInt(String prompt) {
        int value = 0;
        while (value <= 0) {
            value = readInt(prompt);
            if (value <= 0) printError("Please enter a positive number.");
        }
        return value;
    }

    private static double readDouble(String prompt) {
        System.out.print(YELLOW + prompt + RESET);
        while (!scanner.hasNextDouble()) {
            printError("Please enter a valid number.");
            System.out.print(YELLOW + prompt + RESET);
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private static String readStatus() {
        String[] validStatuses = {"Available", "Assigned", "Maintenance", "Retired"};
        String status = "";
        while (true) {
            System.out.print(YELLOW + "  Status (Available / Assigned / Maintenance / Retired): " + RESET);
            status = scanner.nextLine().trim();
            for (String s : validStatuses) {
                if (s.equalsIgnoreCase(status)) return s;
            }
            printError("Invalid status. Please try again.");
        }
    }

    private static int readInt(String prompt) {
        System.out.print(YELLOW + prompt + RESET);
        while (!scanner.hasNextInt()) {
            printError("Please enter a valid number.");
            System.out.print(YELLOW + prompt + RESET);
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
}