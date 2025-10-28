package model;

public class ProductVariant {
    private int variantId;
    private int productId;
    private String size;
    private String color;
    private int quantity;
    private boolean status;
    
    // Additional fields for display purposes
    private String productCode;
    private String productName;
    private String categoryName;
    private Integer categoryId;

    public ProductVariant() {
    }

    public ProductVariant(int variantId, int productId, String size, String color, int quantity, boolean status) {
        this.variantId = variantId;
        this.productId = productId;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.status = status;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
    
    // Helper method to get display name
    public String getDisplayName() {
        StringBuilder sb = new StringBuilder();
        if (productName != null) {
            sb.append(productName);
        }
        if (size != null && !size.isEmpty()) {
            sb.append(" - ").append(size);
        }
        if (color != null && !color.isEmpty()) {
            sb.append(" - ").append(color);
        }
        return sb.toString();
    }
}
