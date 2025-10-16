package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ExportReportDTO - Data Transfer Object for Export Reports
 * Used for generating comprehensive reports with all relevant warehouse data
 * Supports multiple report types: inventory, import/export, revenue, and comprehensive
 * 
 * @author lengo
 */
public class ExportReportDTO {
    // Date information
    private Date reportDate;
    private String dateLabel;
    private String reportPeriod; // "daily", "monthly", "yearly"
    
    // Product information
    private Integer productId;
    private String productCode;
    private String productName;
    private String productUnit;
    private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    
    // Supplier/Customer information
    private Integer supplierId;
    private String supplierName;
    private Integer customerId;
    private String customerName;
    
    // Inventory data
    private Integer currentStock;
    private BigDecimal importPrice;
    private BigDecimal exportPrice;
    private BigDecimal stockValue; // currentStock * importPrice
    
    // Import data
    private Integer importQuantity;
    private BigDecimal importValue;
    private Integer importReceiptCount;
    private BigDecimal avgImportPrice;
    
    // Export data
    private Integer exportQuantity;
    private BigDecimal exportValue;
    private Integer exportReceiptCount;
    private BigDecimal avgExportPrice;
    
    // Calculated fields
    private Integer stockMovement; // importQuantity - exportQuantity
    private BigDecimal profitMargin; // exportValue - importValue
    private BigDecimal profitPercentage; // (profitMargin / importValue) * 100
    private BigDecimal turnoverRatio; // exportQuantity / currentStock
    private BigDecimal averageImportValue; // average import value
    private BigDecimal averageExportValue; // average export value
    
    // User information
    private Integer userId;
    private String userName;
    private String userRole;
    
    // Receipt information
    private Integer receiptId;
    private String receiptType; // "import" or "export"
    private String receiptNote;
    
    public ExportReportDTO() {
        this.importValue = BigDecimal.ZERO;
        this.exportValue = BigDecimal.ZERO;
        this.stockValue = BigDecimal.ZERO;
        this.profitMargin = BigDecimal.ZERO;
        this.profitPercentage = BigDecimal.ZERO;
        this.turnoverRatio = BigDecimal.ZERO;
        this.avgImportPrice = BigDecimal.ZERO;
        this.avgExportPrice = BigDecimal.ZERO;
        this.importPrice = BigDecimal.ZERO;
        this.exportPrice = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
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

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    // Alias methods for compatibility
    public String getUnit() {
        return this.productUnit;
    }

    public void setUnit(String unit) {
        this.productUnit = unit;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
        calculateStockValue();
        calculateTurnoverRatio();
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = (importPrice != null) ? importPrice : BigDecimal.ZERO;
        calculateStockValue();
    }

    public BigDecimal getExportPrice() {
        return exportPrice;
    }

    public void setExportPrice(BigDecimal exportPrice) {
        this.exportPrice = (exportPrice != null) ? exportPrice : BigDecimal.ZERO;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = (stockValue != null) ? stockValue : BigDecimal.ZERO;
    }

    public Integer getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(Integer importQuantity) {
        this.importQuantity = importQuantity;
        calculateStockMovement();
        calculateTurnoverRatio();
    }

    public BigDecimal getImportValue() {
        return importValue;
    }

    public void setImportValue(BigDecimal importValue) {
        this.importValue = (importValue != null) ? importValue : BigDecimal.ZERO;
        calculateProfitMargin();
        calculateProfitPercentage();
    }

    public Integer getImportReceiptCount() {
        return importReceiptCount;
    }

    public void setImportReceiptCount(Integer importReceiptCount) {
        this.importReceiptCount = importReceiptCount;
    }

    public BigDecimal getAvgImportPrice() {
        return avgImportPrice;
    }

    public void setAvgImportPrice(BigDecimal avgImportPrice) {
        this.avgImportPrice = (avgImportPrice != null) ? avgImportPrice : BigDecimal.ZERO;
    }

    public Integer getExportQuantity() {
        return exportQuantity;
    }

    public void setExportQuantity(Integer exportQuantity) {
        this.exportQuantity = exportQuantity;
        calculateStockMovement();
        calculateTurnoverRatio();
    }

    public BigDecimal getExportValue() {
        return exportValue;
    }

    public void setExportValue(BigDecimal exportValue) {
        this.exportValue = (exportValue != null) ? exportValue : BigDecimal.ZERO;
        calculateProfitMargin();
        calculateProfitPercentage();
    }

    public Integer getExportReceiptCount() {
        return exportReceiptCount;
    }

    public void setExportReceiptCount(Integer exportReceiptCount) {
        this.exportReceiptCount = exportReceiptCount;
    }

    public BigDecimal getAvgExportPrice() {
        return avgExportPrice;
    }

    public void setAvgExportPrice(BigDecimal avgExportPrice) {
        this.avgExportPrice = (avgExportPrice != null) ? avgExportPrice : BigDecimal.ZERO;
    }

    public Integer getStockMovement() {
        return stockMovement;
    }

    public void setStockMovement(Integer stockMovement) {
        this.stockMovement = stockMovement;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = (profitMargin != null) ? profitMargin : BigDecimal.ZERO;
    }

    public BigDecimal getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(BigDecimal profitPercentage) {
        this.profitPercentage = (profitPercentage != null) ? profitPercentage : BigDecimal.ZERO;
    }

    public BigDecimal getTurnoverRatio() {
        return turnoverRatio;
    }

    public void setTurnoverRatio(BigDecimal turnoverRatio) {
        this.turnoverRatio = (turnoverRatio != null) ? turnoverRatio : BigDecimal.ZERO;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Integer receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public String getReceiptNote() {
        return receiptNote;
    }

    public void setReceiptNote(String receiptNote) {
        this.receiptNote = receiptNote;
    }

    public BigDecimal getAverageImportValue() {
        return averageImportValue;
    }

    public void setAverageImportValue(BigDecimal averageImportValue) {
        this.averageImportValue = averageImportValue;
    }

    public BigDecimal getAverageExportValue() {
        return averageExportValue;
    }

    public void setAverageExportValue(BigDecimal averageExportValue) {
        this.averageExportValue = averageExportValue;
    }

    // Helper methods for calculations
    public void calculateStockValue() {
        if (currentStock != null && importPrice != null) {
            this.stockValue = importPrice.multiply(BigDecimal.valueOf(currentStock));
        }
    }

    public void calculateStockMovement() {
        if (importQuantity != null && exportQuantity != null) {
            this.stockMovement = importQuantity - exportQuantity;
        }
    }

    public void calculateProfitMargin() {
        if (exportValue != null && importValue != null) {
            this.profitMargin = exportValue.subtract(importValue);
        }
    }

    public void calculateProfitPercentage() {
        if (profitMargin != null && importValue != null && importValue.compareTo(BigDecimal.ZERO) > 0) {
            this.profitPercentage = profitMargin.divide(importValue, 4, BigDecimal.ROUND_HALF_UP)
                                                .multiply(BigDecimal.valueOf(100));
        }
    }

    public void calculateTurnoverRatio() {
        if (exportQuantity != null && currentStock != null && currentStock > 0) {
            this.turnoverRatio = BigDecimal.valueOf(exportQuantity)
                                          .divide(BigDecimal.valueOf(currentStock), 4, BigDecimal.ROUND_HALF_UP);
        }
    }

    @Override
    public String toString() {
        return "ExportReportDTO{" +
                "reportDate=" + reportDate +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", currentStock=" + currentStock +
                ", importQuantity=" + importQuantity +
                ", exportQuantity=" + exportQuantity +
                ", stockMovement=" + stockMovement +
                ", importValue=" + importValue +
                ", exportValue=" + exportValue +
                ", profitMargin=" + profitMargin +
                ", profitPercentage=" + profitPercentage +
                '}';
    }
}
