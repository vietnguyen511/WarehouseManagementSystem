package model;

import java.math.BigDecimal;
import java.util.Date;

public class ExportReceiptListDTO {
    private int exportId;
    private Date date;
    private String customerName;
    private int totalQuantity;
    private BigDecimal totalAmount;
    private String userName;
    private String note;

    public ExportReceiptListDTO() {
    }

    public ExportReceiptListDTO(int exportId, Date date, String customerName, int totalQuantity, 
                               BigDecimal totalAmount, String userName, String note) {
        this.exportId = this.exportId;
        this.date = date;
        this.customerName = customerName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.userName = userName;
        this.note = note;
    }

    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
