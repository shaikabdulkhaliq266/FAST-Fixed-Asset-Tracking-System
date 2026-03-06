package model;

/**
 * ElectronicAsset.java
 * Subclass of Asset for electronic devices (Laptops, Desktops, Printers, etc.)
 * Adds warranty period and brand attributes.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class ElectronicAsset extends Asset {

    private int    warrantyPeriodYears;
    private String brand;

    // Constructor — matches Asset(int, String, String, String, double, String)
    public ElectronicAsset(int assetId, String assetName, String serialNumber,
                           String category, double purchasePrice, String status,
                           int warrantyPeriodYears, String brand) {
        super(assetId, assetName, serialNumber, category, purchasePrice, status);
        this.warrantyPeriodYears = warrantyPeriodYears;
        this.brand = brand;
    }

    // Getters & Setters
    public int    getWarrantyPeriodYears()       { return warrantyPeriodYears; }
    public void   setWarrantyPeriodYears(int w)  { this.warrantyPeriodYears = w; }

    public String getBrand()                     { return brand; }
    public void   setBrand(String brand)         { this.brand = brand; }

    @Override
    public String toString() {
        return super.toString() +
                " | Brand: " + brand +
                " | Warranty: " + warrantyPeriodYears + " year(s)";
    }
}
