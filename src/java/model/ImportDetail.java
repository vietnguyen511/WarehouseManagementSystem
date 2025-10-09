package model;

import java.math.BigDecimal;

public class ImportDetail {
    private int importDetailId;
    private int importId;
    private int productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String productCode;
    private String productName;
    private String unit;
    private String categoryName;
    private Integer categoryId;

    public ImportDetail() {
    }

    public ImportDetail(int importDetailId, int importId, int productId, int quantity, BigDecimal price, BigDecimal amount, String productCode, String productName) {
        this.importDetailId = importDetailId;
        this.importId = importId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
        this.productCode = productCode;
        this.productName = productName;
    }

    public int getImportDetailId() {
        return importDetailId;
    }

    public void setImportDetailId(int importDetailId) {
        this.importDetailId = importDetailId;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
}


