package model;

import java.math.BigDecimal;
import java.util.Date;

public class ImportReceiptListDTO {
    private int importId;
    private Date date;
    private String supplierName;
    private int totalQuantity;
    private BigDecimal totalAmount;
    private String userName;
    private String note;

    public ImportReceiptListDTO() {
    }

    public ImportReceiptListDTO(int importId, Date date, String supplierName, int totalQuantity, 
                               BigDecimal totalAmount, String userName, String note) {
        this.importId = importId;
        this.date = date;
        this.supplierName = supplierName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.userName = userName;
        this.note = note;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
