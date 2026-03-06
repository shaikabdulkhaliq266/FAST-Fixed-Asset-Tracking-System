package model;

/**
 * Asset.java
 * Base class representing a fixed asset in the Asset Tracking System.
 * All asset types (Electronic, Vehicle, etc.) extend this class.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class Asset {

    // ─────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────
    private int    assetId;
    private String assetName;
    private String serialNumber;
    private String category;
    private double purchasePrice;
    private String status;        // "Available", "Assigned", "Maintenance", "Retired"

    // ─────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────
    public Asset(int assetId, String assetName, String serialNumber,
                 String category, double purchasePrice, String status) {

        if (assetName == null || assetName.trim().isEmpty())
            throw new IllegalArgumentException("Asset name cannot be empty.");
        if (serialNumber == null || serialNumber.trim().isEmpty())
            throw new IllegalArgumentException("Serial number cannot be empty.");
        if (purchasePrice < 0)
            throw new IllegalArgumentException("Purchase price cannot be negative.");

        this.assetId       = assetId;
        this.assetName     = assetName.trim();
        this.serialNumber  = serialNumber.trim();
        this.category      = category;
        this.purchasePrice = purchasePrice;
        this.status        = status;
    }

    // ─────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────
    public int    getAssetId()       { return assetId; }
    public String getAssetName()     { return assetName; }
    public String getSerialNumber()  { return serialNumber; }
    public String getCategory()      { return category; }
    public double getPurchasePrice() { return purchasePrice; }
    public String getStatus()        { return status; }

    // ─────────────────────────────────────────────
    // Setters with validation
    // ─────────────────────────────────────────────
    public void setAssetId(int assetId)            { this.assetId = assetId; }

    public void setAssetName(String assetName) {
        if (assetName == null || assetName.trim().isEmpty())
            throw new IllegalArgumentException("Asset name cannot be empty.");
        this.assetName = assetName.trim();
    }

    public void setSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.trim().isEmpty())
            throw new IllegalArgumentException("Serial number cannot be empty.");
        this.serialNumber = serialNumber.trim();
    }

    public void setCategory(String category)       { this.category = category; }

    public void setPurchasePrice(double purchasePrice) {
        if (purchasePrice < 0)
            throw new IllegalArgumentException("Purchase price cannot be negative.");
        this.purchasePrice = purchasePrice;
    }

    public void setStatus(String status)           { this.status = status; }

    // ─────────────────────────────────────────────
    // toString
    // ─────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
                "Asset ID: %d | Name: %-20s | Serial: %-15s | Category: %-15s | Price: $%.2f | Status: %s",
                assetId, assetName, serialNumber, category, purchasePrice, status
        );
    }
}