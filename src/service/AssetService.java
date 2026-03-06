package service;

import database.Dataconnection;
import model.Asset;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AssetService.java
 * Handles all database operations related to Assets.
 * Includes full CRUD, search, and duplicate serial number detection.
 *
 * Muffakham Jah College of Engineering and Technology
 * MJ-Industry Ready Program | Academic Year 2025-26
 */
public class AssetService {

    private static final Logger LOGGER = Logger.getLogger(AssetService.class.getName());

    // ─────────────────────────────────────────────
    // ADD asset — checks for duplicate serial first
    // ─────────────────────────────────────────────
    public boolean addAsset(Asset asset) {

        // Integrity check — duplicate serial number detection
        if (isSerialNumberDuplicate(asset.getSerialNumber())) {
            LOGGER.log(Level.WARNING, "Duplicate serial number: {0}", asset.getSerialNumber());
            System.out.println("[ERROR] Serial number already exists: " + asset.getSerialNumber());
            return false;
        }

        String sql = "INSERT INTO Asset (assetName, serialNumber, category, purchasePrice, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, asset.getAssetName());
            stmt.setString(2, asset.getSerialNumber());
            stmt.setString(3, asset.getCategory());
            stmt.setDouble(4, asset.getPurchasePrice());
            stmt.setString(5, asset.getStatus());
            stmt.executeUpdate();

            LOGGER.log(Level.INFO, "Asset added: {0}", asset.getAssetName());
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add asset: {0}", e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // GET all assets
    // ─────────────────────────────────────────────
    public List<Asset> getAllAssets() {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM Asset";

        try (Connection conn = Dataconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                assets.add(new Asset(
                        rs.getInt("assetId"),
                        rs.getString("assetName"),
                        rs.getString("serialNumber"),
                        rs.getString("category"),
                        rs.getDouble("purchasePrice"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve assets: {0}", e.getMessage());
        }
        return assets;
    }

    // ─────────────────────────────────────────────
    // GET assets by status (Available, Assigned, etc.)
    // ─────────────────────────────────────────────
    public List<Asset> getAssetsByStatus(String status) {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM Asset WHERE status = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assets.add(new Asset(
                        rs.getInt("assetId"),
                        rs.getString("assetName"),
                        rs.getString("serialNumber"),
                        rs.getString("category"),
                        rs.getDouble("purchasePrice"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve assets by status: {0}", e.getMessage());
        }
        return assets;
    }

    // ─────────────────────────────────────────────
    // GET assets by category
    // ─────────────────────────────────────────────
    public List<Asset> getAssetsByCategory(String category) {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM Asset WHERE category = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assets.add(new Asset(
                        rs.getInt("assetId"),
                        rs.getString("assetName"),
                        rs.getString("serialNumber"),
                        rs.getString("category"),
                        rs.getDouble("purchasePrice"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve assets by category: {0}", e.getMessage());
        }
        return assets;
    }

    // ─────────────────────────────────────────────
    // SEARCH assets by name or serial number
    // ─────────────────────────────────────────────
    public List<Asset> searchAssets(String keyword) {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM Asset WHERE assetName LIKE ? OR serialNumber LIKE ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assets.add(new Asset(
                        rs.getInt("assetId"),
                        rs.getString("assetName"),
                        rs.getString("serialNumber"),
                        rs.getString("category"),
                        rs.getDouble("purchasePrice"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search assets: {0}", e.getMessage());
        }
        return assets;
    }

    // ─────────────────────────────────────────────
    // UPDATE asset status only
    // ─────────────────────────────────────────────
    public void updateAssetStatus(int assetId, String newStatus) {
        String sql = "UPDATE Asset SET status = ? WHERE assetId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, assetId);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Asset status updated. ID: {0}", assetId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update asset status: {0}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // UPDATE full asset details
    // ─────────────────────────────────────────────
    public void updateAsset(int assetId, String newName, String newCategory,
                            double newPrice, String newStatus) {
        String sql = "UPDATE Asset SET assetName = ?, category = ?, " +
                "purchasePrice = ?, status = ? WHERE assetId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, newCategory);
            stmt.setDouble(3, newPrice);
            stmt.setString(4, newStatus);
            stmt.setInt(5, assetId);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Asset updated. ID: {0}", assetId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update asset: {0}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // DELETE asset
    // ─────────────────────────────────────────────
    public void deleteAsset(int assetId) {
        String sql = "DELETE FROM Asset WHERE assetId = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assetId);
            stmt.executeUpdate();
            LOGGER.log(Level.INFO, "Asset deleted. ID: {0}", assetId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete asset: {0}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // CHECK duplicate serial number — Integrity check
    // Required by spec: "ensure no duplicate serial numbers"
    // ─────────────────────────────────────────────
    public boolean isSerialNumberDuplicate(String serialNumber) {
        String sql = "SELECT COUNT(*) FROM Asset WHERE serialNumber = ?";

        try (Connection conn = Dataconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, serialNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check serial number: {0}", e.getMessage());
        }
        return false;
    }
}