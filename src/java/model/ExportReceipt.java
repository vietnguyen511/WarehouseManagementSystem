package model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ExportReceipt {
    private int exportId;
    private int customerId;
    private int userId;
    private Date date;
    private int totalQuantity;
    private BigDecimal totalAmount;
    private String note;
    private Date createdAt;

    // For displaying customer/user names
    private String customerName;
    private String userName;

    // List of export details
    private List<ExportDetail> details;

    public ExportReceipt() {
    }

    // Getters and Setters
    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ExportDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ExportDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ExportReceipt{" +
                "exportId=" + exportId +
                ", customerId=" + customerId +
                ", userId=" + userId +
                ", date=" + date +
                ", totalQuantity=" + totalQuantity +
                ", totalAmount=" + totalAmount +
                ", note='" + note + '\'' +
                ", customerName='" + customerName + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
