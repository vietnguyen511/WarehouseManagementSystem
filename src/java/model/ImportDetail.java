package model;

public class ImportDetail {

    private int importDetailId;
    private int importId;
    private int productId;
    private int quantity;
    private double price;
    private double amount;

    public ImportDetail() {
    }

    public ImportDetail(int importDetailId, int importId, int productId, int quantity, double price, double amount) {
        this.importDetailId = importDetailId;
        this.importId = importId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
