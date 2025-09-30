/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 * InventoryItem Model - Represents current inventory data for reporting
 * Combines data from Products and Categories tables
 * 
 * @author lengo
 */
public class InventoryItem {
    private int productId;
    private String productCode;
    private String productName;
    private int quantityOnHand;
    private String unitName;
    private BigDecimal inventoryValue;
    private int categoryId;
    private String categoryName;
    private BigDecimal importPrice;
    private BigDecimal exportPrice;
    private boolean status;
    private int reorderThreshold; // For determining stock status (low/ok/out)

    public InventoryItem() {
        this.reorderThreshold = 20; // Default threshold
    }

    public InventoryItem(int productId, String productCode, String productName, 
                        int quantityOnHand, String unitName, BigDecimal inventoryValue,
                        int categoryId, String categoryName, BigDecimal importPrice, 
                        BigDecimal exportPrice, boolean status) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.quantityOnHand = quantityOnHand;
        this.unitName = unitName;
        this.inventoryValue = inventoryValue;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.importPrice = importPrice;
        this.exportPrice = exportPrice;
        this.status = status;
        this.reorderThreshold = 20; // Default threshold
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getInventoryValue() {
        return inventoryValue;
    }

    public void setInventoryValue(BigDecimal inventoryValue) {
        this.inventoryValue = inventoryValue;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public BigDecimal getExportPrice() {
        return exportPrice;
    }

    public void setExportPrice(BigDecimal exportPrice) {
        this.exportPrice = exportPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    /**
     * Get stock status as string
     * @return "In Stock", "Low Stock", or "Out of Stock"
     */
    public String getStockStatus() {
        if (quantityOnHand == 0) {
            return "Out of Stock";
        } else if (quantityOnHand <= reorderThreshold) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "productId=" + productId +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", quantityOnHand=" + quantityOnHand +
                ", unitName='" + unitName + '\'' +
                ", inventoryValue=" + inventoryValue +
                ", categoryName='" + categoryName + '\'' +
                ", status=" + status +
                '}';
    }
}

