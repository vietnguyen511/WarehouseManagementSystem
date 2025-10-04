package model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String code;
    private String name;
    private String unit;
    private int quantity;
    private BigDecimal importPrice;
    private BigDecimal exportPrice;
    private boolean status;
    private Integer categoryId;
    private String categoryName;

    public Product() {
    }

    public Product(int productId, String code, String name, String unit, int quantity, BigDecimal importPrice, BigDecimal exportPrice, boolean status) {
        this.productId = productId;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.importPrice = importPrice;
        this.exportPrice = exportPrice;
        this.status = status;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
}


