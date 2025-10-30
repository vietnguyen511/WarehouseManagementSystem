package model;

import java.math.BigDecimal;

public class ExportDetail {
    private int exportDetailId;
    private int exportId;
    private int variantId;  // Changed from productId to variantId
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String unit;
    private String categoryName;
    // For displaying product info
    private String productCode;
    private String productName;
    private String size;     // Added for ProductVariant
    private String color;    // Added for ProductVariant

    public ExportDetail() {
    }

    // Getters and Setters
    public int getExportDetailId() {
        return exportDetailId;
    }

    public void setExportDetailId(int exportDetailId) {
        this.exportDetailId = exportDetailId;
    }

    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }
    
    // Legacy getter for backward compatibility
    public int getProductId() {
        return variantId; // Return variantId for backward compatibility
    }

    // Legacy setter for backward compatibility
    public void setProductId(int productId) {
        this.variantId = productId; // Set to variantId for backward compatibility
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    
    @Override
    public String toString() {
        return "ExportDetail{" +
                "exportDetailId=" + exportDetailId +
                ", exportId=" + exportId +
                ", variantId=" + variantId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
