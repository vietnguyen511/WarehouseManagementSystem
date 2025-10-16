package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * RevenueStatDTO - Data Transfer Object for Revenue Statistics
 * Used for displaying revenue reports with date, quantity, value, and product/category information
 * 
 * @author lengo
 */
public class RevenueStatDTO {
    private Date date;
    private String dateLabel; // Formatted date for display
    private int productId;
    private String productCode;
    private String productName;
    private int categoryId;
    private String categoryCode;
    private String categoryName;
    private int totalQuantity;
    private BigDecimal totalValue;
    private int receiptCount; // Number of export receipts for this period/product
    private BigDecimal averageValue; // Average value per unit

    public RevenueStatDTO() {
        this.totalValue = BigDecimal.ZERO;
        this.averageValue = BigDecimal.ZERO;
    }

    public RevenueStatDTO(Date date, int productId, String productCode, String productName,
                         int categoryId, String categoryCode, String categoryName,
                         int totalQuantity, BigDecimal totalValue, int receiptCount) {
        this.date = date;
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.totalQuantity = totalQuantity;
        this.totalValue = (totalValue != null) ? totalValue : BigDecimal.ZERO;
        this.receiptCount = receiptCount;
        this.averageValue = (totalQuantity > 0 && this.totalValue.compareTo(BigDecimal.ZERO) > 0) 
                           ? this.totalValue.divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP)
                           : BigDecimal.ZERO;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
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

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        // Recalculate average value
        if (totalQuantity > 0 && this.totalValue.compareTo(BigDecimal.ZERO) > 0) {
            this.averageValue = this.totalValue.divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.averageValue = BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = (totalValue != null) ? totalValue : BigDecimal.ZERO;
        // Recalculate average value
        if (this.totalQuantity > 0 && this.totalValue.compareTo(BigDecimal.ZERO) > 0) {
            this.averageValue = this.totalValue.divide(BigDecimal.valueOf(this.totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.averageValue = BigDecimal.ZERO;
        }
    }

    public int getReceiptCount() {
        return receiptCount;
    }

    public void setReceiptCount(int receiptCount) {
        this.receiptCount = receiptCount;
    }

    public BigDecimal getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(BigDecimal averageValue) {
        this.averageValue = (averageValue != null) ? averageValue : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "RevenueStatDTO{" +
                "date=" + date +
                ", dateLabel='" + dateLabel + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", totalValue=" + totalValue +
                ", receiptCount=" + receiptCount +
                ", averageValue=" + averageValue +
                '}';
    }
}
