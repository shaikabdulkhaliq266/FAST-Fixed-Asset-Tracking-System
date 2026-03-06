package model;

/**
 * VehicleAsset.java
 * Subclass of Asset for company-owned vehicles (Cars, Vans, Motorcycles, etc.)
 * Adds registration number and mileage attributes.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class VehicleAsset extends Asset {

    private String registrationNumber;
    private double currentMileage;

    // Constructor — matches Asset(int, String, String, String, double, String)
    public VehicleAsset(int assetId, String assetName, String serialNumber,
                        String category, double purchasePrice, String status,
                        String registrationNumber, double currentMileage) {
        super(assetId, assetName, serialNumber, category, purchasePrice, status);
        this.registrationNumber = registrationNumber;
        this.currentMileage     = currentMileage;
    }

    // Getters & Setters
    public String getRegistrationNumber()        { return registrationNumber; }
    public void   setRegistrationNumber(String r){ this.registrationNumber = r; }

    public double getCurrentMileage()            { return currentMileage; }
    public void   setCurrentMileage(double m)    { this.currentMileage = m; }

    @Override
    public String toString() {
        return super.toString() +
                " | Reg No: " + registrationNumber +
                " | Mileage: " + currentMileage + " km";
    }
}
