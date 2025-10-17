
package model;

import java.sql.Date;

public class ExportReceipt {
    private int exportId;
    private int customerId;
    private int userId;
    private Date date;
    private int totalQuantity;
    private double totalAmount;
    private String note;
    private String createdAt;
    
    //Contructor

    public ExportReceipt() {
    }
    public ExportReceipt(int customerId, int userId, Date date, int totalQuantity, double totalAmount, String note, String createdAt) {
        this.customerId = customerId;
        this.userId = userId;
        this.date = date;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.note = note;
        this.createdAt = createdAt;
    }
       
    

    public ExportReceipt(int exportId, int customerId, int userId, Date date, int totalQuantity, double totalAmount, String note, String createdAt) {
        this.exportId = exportId;
        this.customerId = customerId;
        this.userId = userId;
        this.date = date;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.note = note;
        this.createdAt = createdAt;
    }
    
    //Getter Setter

    public int getExportId() {
        return exportId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getNote() {
        return note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
