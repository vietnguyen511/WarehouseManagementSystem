
package model;

public class ExportDetail {
    private int exportDetailId;
    private int exportId;
    private int productId;
    private int quantity;
    private double price;
    private double amount;
    
    //Contructor

    public ExportDetail() {
    }

    public ExportDetail(int exportDetailId, int exportId, int productId, int quantity, double price, double amount) {
        this.exportDetailId = exportDetailId;
        this.exportId = exportId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
    }
    
    //Getter Setter

    public int getExportDetailId() {
        return exportDetailId;
    }

    public int getExportId() {
        return exportId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public void setExportDetailId(int exportDetailId) {
        this.exportDetailId = exportDetailId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    
}
