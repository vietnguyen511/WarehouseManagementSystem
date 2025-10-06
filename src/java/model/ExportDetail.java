package model;

import java.math.BigDecimal;

public class ExportDetail {
    private int exportDetailId;
    private int exportId;
    private int productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;
    
    // For displaying product info
    private String productCode;
    private String productName;

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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    @Override
    public String toString() {
        return "ExportDetail{" +
                "exportDetailId=" + exportDetailId +
                ", exportId=" + exportId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}

