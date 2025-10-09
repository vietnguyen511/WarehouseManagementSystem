package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.ImportExportStatDTO;

/**
 * StatisticsDAO - Data Access Object for warehouse statistics and reporting
 * Handles complex queries for import/export analytics
 * 
 * @author lengo
 */
public class StatisticsDAO extends DBContext {
    
    public StatisticsDAO() {
        super();
    }

    public StatisticsDAO(Connection connection) {
        super(connection);
    }

    /**
     * Get import/export statistics for a date range
     * @param startDate Start date (null for 30 days ago)
     * @param endDate End date (null for today)
     * @param groupBy "day" or "month"
     * @return List of statistics grouped by date
     */
    public List<ImportExportStatDTO> getImportExportStatistics(Date startDate, Date endDate, String groupBy) {
        List<ImportExportStatDTO> stats = new ArrayList<>();
        
        // Default to last 30 days if no dates provided
        if (endDate == null) {
            endDate = new Date();
        }
        if (startDate == null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
            startDate = cal.getTime();
        }
        
        // Determine date format and grouping
        String dateFormat = "day".equalsIgnoreCase(groupBy) ? "yyyy-MM-dd" : "yyyy-MM";
        
        StringBuilder sql = new StringBuilder();
        sql.append("WITH DateSeries AS ( ");
        sql.append("    SELECT CAST(? AS DATE) AS report_date ");
        sql.append("    UNION ALL ");
        sql.append("    SELECT DATEADD(DAY, 1, report_date) ");
        sql.append("    FROM DateSeries ");
        sql.append("    WHERE report_date < CAST(? AS DATE) ");
        sql.append("), ");
        sql.append("ImportStats AS ( ");
        sql.append("    SELECT ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("        FORMAT(ir.date, 'yyyy-MM') AS period, ");
        } else {
            sql.append("        CAST(ir.date AS DATE) AS period, ");
        }
        sql.append("        COUNT(DISTINCT ir.import_id) AS receipt_count, ");
        sql.append("        ISNULL(SUM(id.quantity), 0) AS total_quantity, ");
        sql.append("        ISNULL(SUM(id.amount), 0) AS total_value ");
        sql.append("    FROM ImportReceipts ir ");
        sql.append("    LEFT JOIN ImportDetails id ON ir.import_id = id.import_id ");
        sql.append("    WHERE ir.date >= ? AND ir.date <= ? ");
        sql.append("    GROUP BY ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("        FORMAT(ir.date, 'yyyy-MM') ");
        } else {
            sql.append("        CAST(ir.date AS DATE) ");
        }
        sql.append("), ");
        sql.append("ExportStats AS ( ");
        sql.append("    SELECT ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("        FORMAT(er.date, 'yyyy-MM') AS period, ");
        } else {
            sql.append("        CAST(er.date AS DATE) AS period, ");
        }
        sql.append("        COUNT(DISTINCT er.export_id) AS receipt_count, ");
        sql.append("        ISNULL(SUM(ed.quantity), 0) AS total_quantity, ");
        sql.append("        ISNULL(SUM(ed.amount), 0) AS total_value ");
        sql.append("    FROM ExportReceipts er ");
        sql.append("    LEFT JOIN ExportDetails ed ON er.export_id = ed.export_id ");
        sql.append("    WHERE er.date >= ? AND er.date <= ? ");
        sql.append("    GROUP BY ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("        FORMAT(er.date, 'yyyy-MM') ");
        } else {
            sql.append("        CAST(er.date AS DATE) ");
        }
        sql.append(") ");
        sql.append("SELECT ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("    ds.report_date, ");
            sql.append("    FORMAT(ds.report_date, 'yyyy-MM') AS period, ");
        } else {
            sql.append("    ds.report_date AS period, ");
        }
        sql.append("    ISNULL(i.receipt_count, 0) AS import_receipts, ");
        sql.append("    ISNULL(i.total_quantity, 0) AS import_qty, ");
        sql.append("    ISNULL(i.total_value, 0.0) AS import_val, ");
        sql.append("    ISNULL(e.receipt_count, 0) AS export_receipts, ");
        sql.append("    ISNULL(e.total_quantity, 0) AS export_qty, ");
        sql.append("    ISNULL(e.total_value, 0.0) AS export_val ");
        sql.append("FROM DateSeries ds ");
        sql.append("LEFT JOIN ImportStats i ON ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("    FORMAT(ds.report_date, 'yyyy-MM') = i.period ");
        } else {
            sql.append("    ds.report_date = i.period ");
        }
        sql.append("LEFT JOIN ExportStats e ON ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("    FORMAT(ds.report_date, 'yyyy-MM') = e.period ");
        } else {
            sql.append("    ds.report_date = e.period ");
        }
        sql.append("ORDER BY ");
        if ("month".equalsIgnoreCase(groupBy)) {
            sql.append("    ds.report_date ");
        } else {
            sql.append("    period ");
        }
        sql.append("OPTION (MAXRECURSION 0)");
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql.toString());
            st.setQueryTimeout(60);
            
            // Set parameters
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
            
            st.setDate(1, sqlStartDate);  // DateSeries start
            st.setDate(2, sqlEndDate);    // DateSeries end
            st.setDate(3, sqlStartDate);  // ImportStats filter
            st.setDate(4, sqlEndDate);    // ImportStats filter
            st.setDate(5, sqlStartDate);  // ExportStats filter
            st.setDate(6, sqlEndDate);    // ExportStats filter
            
            rs = st.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("month".equalsIgnoreCase(groupBy) ? "MMM yyyy" : "dd-MMM");
            
            while (rs.next()) {
                ImportExportStatDTO stat = new ImportExportStatDTO();
                
                Date periodDate = rs.getDate("month".equalsIgnoreCase(groupBy) ? "report_date" : "period");
                stat.setDate(periodDate);
                stat.setDateLabel(sdf.format(periodDate));
                
                stat.setImportQuantity(rs.getInt("import_qty"));
                stat.setExportQuantity(rs.getInt("export_qty"));
                
                BigDecimal importVal = rs.getBigDecimal("import_val");
                BigDecimal exportVal = rs.getBigDecimal("export_val");
                stat.setImportValue(importVal != null ? importVal : BigDecimal.ZERO);
                stat.setExportValue(exportVal != null ? exportVal : BigDecimal.ZERO);
                
                stat.setImportReceiptCount(rs.getInt("import_receipts"));
                stat.setExportReceiptCount(rs.getInt("export_receipts"));
                
                // Calculate differences
                stat.setStockDifference(stat.getImportQuantity() - stat.getExportQuantity());
                stat.setValueDifference(stat.getImportValue().subtract(stat.getExportValue()));
                
                stats.add(stat);
            }
        } catch (SQLException e) {
            System.out.println("Error in getImportExportStatistics: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return stats;
    }

    /**
     * Get summary statistics for dashboard
     * @return Array: [totalImports, totalExports, totalImportValue, totalExportValue, netProfit]
     */
    public Object[] getSummaryStatistics() {
        String sql = 
            "SELECT " +
            "    (SELECT ISNULL(SUM(total_quantity), 0) FROM ImportReceipts) AS total_imports, " +
            "    (SELECT ISNULL(SUM(total_quantity), 0) FROM ExportReceipts) AS total_exports, " +
            "    (SELECT ISNULL(SUM(total_amount), 0) FROM ImportReceipts) AS total_import_value, " +
            "    (SELECT ISNULL(SUM(total_amount), 0) FROM ExportReceipts) AS total_export_value";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setQueryTimeout(30);
            rs = st.executeQuery();
            
            if (rs.next()) {
                int totalImports = rs.getInt("total_imports");
                int totalExports = rs.getInt("total_exports");
                BigDecimal importValue = rs.getBigDecimal("total_import_value");
                BigDecimal exportValue = rs.getBigDecimal("total_export_value");
                BigDecimal netProfit = exportValue.subtract(importValue);
                
                return new Object[]{totalImports, totalExports, importValue, exportValue, netProfit};
            }
        } catch (SQLException e) {
            System.out.println("Error in getSummaryStatistics: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return new Object[]{0, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
    }
}

