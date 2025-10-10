package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ImportExportStatDTO - Data Transfer Object for Import/Export Statistics
 * Used for displaying daily/monthly import and export summaries
 * 
 * @author lengo
 */
public class ImportExportStatDTO {
    private Date date;
    private String dateLabel; // Formatted date for display
    private int importQuantity;
    private int exportQuantity;
    private BigDecimal importValue;
    private BigDecimal exportValue;
    private int stockDifference; // importQuantity - exportQuantity
    private BigDecimal valueDifference; // importValue - exportValue
    private int importReceiptCount;
    private int exportReceiptCount;

    public ImportExportStatDTO() {
        this.importValue = BigDecimal.ZERO;
        this.exportValue = BigDecimal.ZERO;
        this.valueDifference = BigDecimal.ZERO;
    }

    public ImportExportStatDTO(Date date, int importQuantity, int exportQuantity, 
                              BigDecimal importValue, BigDecimal exportValue) {
        this.date = date;
        this.importQuantity = importQuantity;
        this.exportQuantity = exportQuantity;
        this.importValue = importValue;
        this.exportValue = exportValue;
        this.stockDifference = importQuantity - exportQuantity;
        this.valueDifference = importValue.subtract(exportValue);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public int getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(int importQuantity) {
        this.importQuantity = importQuantity;
        this.stockDifference = this.importQuantity - this.exportQuantity;
    }

    public int getExportQuantity() {
        return exportQuantity;
    }

    public void setExportQuantity(int exportQuantity) {
        this.exportQuantity = exportQuantity;
        this.stockDifference = this.importQuantity - this.exportQuantity;
    }

    public BigDecimal getImportValue() {
        return importValue;
    }

    public void setImportValue(BigDecimal importValue) {
        this.importValue = (importValue != null) ? importValue : BigDecimal.ZERO;
        if (this.exportValue != null) {
            this.valueDifference = this.importValue.subtract(this.exportValue);
        }
    }

    public BigDecimal getExportValue() {
        return exportValue;
    }

    public void setExportValue(BigDecimal exportValue) {
        this.exportValue = (exportValue != null) ? exportValue : BigDecimal.ZERO;
        if (this.importValue != null) {
            this.valueDifference = this.importValue.subtract(this.exportValue);
        }
    }

    public int getStockDifference() {
        return stockDifference;
    }

    public void setStockDifference(int stockDifference) {
        this.stockDifference = stockDifference;
    }

    public BigDecimal getValueDifference() {
        return valueDifference;
    }

    public void setValueDifference(BigDecimal valueDifference) {
        this.valueDifference = valueDifference;
    }

    public int getImportReceiptCount() {
        return importReceiptCount;
    }

    public void setImportReceiptCount(int importReceiptCount) {
        this.importReceiptCount = importReceiptCount;
    }

    public int getExportReceiptCount() {
        return exportReceiptCount;
    }

    public void setExportReceiptCount(int exportReceiptCount) {
        this.exportReceiptCount = exportReceiptCount;
    }

    @Override
    public String toString() {
        return "ImportExportStatDTO{" +
                "date=" + date +
                ", dateLabel='" + dateLabel + '\'' +
                ", importQuantity=" + importQuantity +
                ", exportQuantity=" + exportQuantity +
                ", stockDifference=" + stockDifference +
                ", importValue=" + importValue +
                ", exportValue=" + exportValue +
                ", valueDifference=" + valueDifference +
                '}';
    }
}

