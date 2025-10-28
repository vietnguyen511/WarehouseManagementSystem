package model;

import java.math.BigDecimal;

/**
 * ProductVariantInventoryItem - Represents inventory at variant level
 * Shows individual variant (size/color) stock information
 * 
 * @author lengo
 */
public class ProductVariantInventoryItem {
    private int variantId;
    private int productId;
    private String productCode;
    private String productName;
    private String size;
    private String color;
    private int quantityOnHand;
    private BigDecimal importPrice;
    private BigDecimal exportPrice;
    private boolean status;
    private int categoryId;
    private String categoryCode;
    private String categoryName;
    
    public ProductVariantInventoryItem() {
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
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

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    /**
     * Get combined size-color string for display
     */
    public String getVariantInfo() {
        StringBuilder sb = new StringBuilder();
        if (size != null && !size.isEmpty()) {
            sb.append(size);
        }
        if (color != null && !color.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(color);
        }
        return sb.length() > 0 ? sb.toString() : "Standard";
    }
}

