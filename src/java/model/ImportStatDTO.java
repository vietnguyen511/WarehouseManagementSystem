package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for Import Statistics
 * Contains statistics for a specific period (day/month)
 */
public class ImportStatDTO {
    private String period; // Date or month in format YYYY-MM-DD or YYYY-MM
    private Date periodDate; // Parsed date for the period
    private int receiptCount; // Number of import receipts
    private int totalQuantity; // Total quantity imported
    private BigDecimal totalAmount; // Total value imported
    
    public ImportStatDTO() {
    }
    
    public ImportStatDTO(String period, int receiptCount, int totalQuantity, BigDecimal totalAmount) {
        this.period = period;
        this.receiptCount = receiptCount;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public Date getPeriodDate() {
        return periodDate;
    }
    
    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }
    
    public int getReceiptCount() {
        return receiptCount;
    }
    
    public void setReceiptCount(int receiptCount) {
        this.receiptCount = receiptCount;
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
}

