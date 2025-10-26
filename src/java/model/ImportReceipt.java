package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportReceipt {
    private int importId;
    private int supplierId;
    private int userId;
    private Date date;
    private int totalQuantity;
    private BigDecimal totalAmount;
    private String note;
    private List<ImportDetail> details = new ArrayList<>();
    
    // Additional fields for display
    private String supplierName;
    private String userName;

    public ImportReceipt() {
    }

    public ImportReceipt(int importId, int supplierId, int userId, Date date, int totalQuantity, BigDecimal totalAmount, String note) {
        this.importId = importId;
        this.supplierId = supplierId;
        this.userId = userId;
        this.date = date;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.note = note;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<ImportDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ImportDetail> details) {
        this.details = details;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}


