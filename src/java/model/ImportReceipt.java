package model;

import java.sql.Date;

public class ImportReceipt {

    private int importId;
    private int supplierId;
    private int userId;
    private Date date;
    private int totalQuantity;
    private double totalAmount;
    private String note;
    private Date createdAt;

    public ImportReceipt() {
    }

    public ImportReceipt(int importId, int supplierId, int userId, Date date, int totalQuantity, double totalAmount, String note, Date createdAt) {
        this.importId = importId;
        this.supplierId = supplierId;
        this.userId = userId;
        this.date = date;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.note = note;
        this.createdAt = createdAt;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
