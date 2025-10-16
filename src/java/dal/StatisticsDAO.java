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
import model.RevenueStatDTO;
import model.ExportReportDTO;

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

    /**
     * Get revenue statistics for a date range with optional product/category filtering
     * @param startDate Start date (null for 30 days ago)
     * @param endDate End date (null for today)
     * @param groupBy "day", "month", "product", or "category"
     * @param productId Product ID filter (null for all products)
     * @param categoryId Category ID filter (null for all categories)
     * @return List of revenue statistics
     */
    public List<RevenueStatDTO> getRevenueStatistics(Date startDate, Date endDate, String groupBy, 
                                                    Integer productId, Integer categoryId) {
        List<RevenueStatDTO> stats = new ArrayList<>();
        
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
        
        StringBuilder sql = new StringBuilder();
        
        // Base query for revenue data
        sql.append("SELECT ");
        
        // Select fields based on grouping
        if ("product".equalsIgnoreCase(groupBy)) {
            sql.append("    NULL AS report_date, ");
            sql.append("    'All Periods' AS date_label, ");
            sql.append("    p.product_id, ");
            sql.append("    p.code AS product_code, ");
            sql.append("    p.name AS product_name, ");
            sql.append("    p.category_id, ");
            sql.append("    c.code AS category_code, ");
            sql.append("    c.name AS category_name, ");
        } else if ("category".equalsIgnoreCase(groupBy)) {
            sql.append("    NULL AS report_date, ");
            sql.append("    'All Periods' AS date_label, ");
            sql.append("    NULL AS product_id, ");
            sql.append("    NULL AS product_code, ");
            sql.append("    NULL AS product_name, ");
            sql.append("    c.category_id, ");
            sql.append("    c.code AS category_code, ");
            sql.append("    c.name AS category_name, ");
        } else {
            // Date-based grouping (day/month)
            if ("month".equalsIgnoreCase(groupBy)) {
                sql.append("    CAST(er.date AS DATE) AS report_date, ");
                sql.append("    FORMAT(er.date, 'MMM yyyy') AS date_label, ");
            } else {
                sql.append("    CAST(er.date AS DATE) AS report_date, ");
                sql.append("    FORMAT(er.date, 'dd-MMM') AS date_label, ");
            }
            sql.append("    NULL AS product_id, ");
            sql.append("    NULL AS product_code, ");
            sql.append("    NULL AS product_name, ");
            sql.append("    NULL AS category_id, ");
            sql.append("    NULL AS category_code, ");
            sql.append("    NULL AS category_name, ");
        }
        
        sql.append("    ISNULL(SUM(ed.quantity), 0) AS total_quantity, ");
        sql.append("    ISNULL(SUM(ed.amount), 0) AS total_value, ");
        sql.append("    COUNT(DISTINCT er.export_id) AS receipt_count ");
        sql.append("FROM ExportReceipts er ");
        sql.append("INNER JOIN ExportDetails ed ON er.export_id = ed.export_id ");
        sql.append("INNER JOIN Products p ON ed.product_id = p.product_id ");
        sql.append("INNER JOIN Categories c ON p.category_id = c.category_id ");
        sql.append("WHERE er.date >= ? AND er.date <= ? ");
        
        // Add filters
        if (productId != null) {
            sql.append("AND p.product_id = ? ");
        }
        if (categoryId != null) {
            sql.append("AND c.category_id = ? ");
        }
        
        // Group by clause
        sql.append("GROUP BY ");
        if ("product".equalsIgnoreCase(groupBy)) {
            sql.append("    p.product_id, p.code, p.name, p.category_id, c.code, c.name ");
        } else if ("category".equalsIgnoreCase(groupBy)) {
            sql.append("    c.category_id, c.code, c.name ");
        } else {
            if ("month".equalsIgnoreCase(groupBy)) {
                sql.append("    CAST(er.date AS DATE), FORMAT(er.date, 'MMM yyyy') ");
            } else {
                sql.append("    CAST(er.date AS DATE), FORMAT(er.date, 'dd-MMM') ");
            }
        }
        
        sql.append("ORDER BY ");
        if ("product".equalsIgnoreCase(groupBy)) {
            sql.append("    total_value DESC ");
        } else if ("category".equalsIgnoreCase(groupBy)) {
            sql.append("    total_value DESC ");
        } else {
            sql.append("    report_date ");
        }
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql.toString());
            st.setQueryTimeout(60);
            
            // Set parameters
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
            
            int paramIndex = 1;
            st.setDate(paramIndex++, sqlStartDate);
            st.setDate(paramIndex++, sqlEndDate);
            
            if (productId != null) {
                st.setInt(paramIndex++, productId);
            }
            if (categoryId != null) {
                st.setInt(paramIndex++, categoryId);
            }
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                RevenueStatDTO stat = new RevenueStatDTO();
                
                // Set date information
                Date reportDate = rs.getDate("report_date");
                stat.setDate(reportDate);
                stat.setDateLabel(rs.getString("date_label"));
                
                // Set product information
                Integer prodId = (Integer) rs.getObject("product_id");
                if (prodId != null) {
                    stat.setProductId(prodId);
                    stat.setProductCode(rs.getString("product_code"));
                    stat.setProductName(rs.getString("product_name"));
                }
                
                // Set category information
                Integer catId = (Integer) rs.getObject("category_id");
                if (catId != null) {
                    stat.setCategoryId(catId);
                    stat.setCategoryCode(rs.getString("category_code"));
                    stat.setCategoryName(rs.getString("category_name"));
                }
                
                // Set revenue data
                stat.setTotalQuantity(rs.getInt("total_quantity"));
                BigDecimal totalValue = rs.getBigDecimal("total_value");
                stat.setTotalValue(totalValue != null ? totalValue : BigDecimal.ZERO);
                stat.setReceiptCount(rs.getInt("receipt_count"));
                
                stats.add(stat);
            }
        } catch (SQLException e) {
            System.out.println("Error in getRevenueStatistics: " + e.getMessage());
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
     * Get revenue summary statistics for dashboard
     * @param startDate Start date (null for 30 days ago)
     * @param endDate End date (null for today)
     * @param productId Product ID filter (null for all products)
     * @param categoryId Category ID filter (null for all categories)
     * @return Array: [totalRevenue, totalQuantity, totalReceipts, averageOrderValue, topProductRevenue]
     */
    public Object[] getRevenueSummaryStatistics(Date startDate, Date endDate, Integer productId, Integer categoryId) {
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
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    ISNULL(SUM(ed.amount), 0) AS total_revenue, ");
        sql.append("    ISNULL(SUM(ed.quantity), 0) AS total_quantity, ");
        sql.append("    COUNT(DISTINCT er.export_id) AS total_receipts, ");
        sql.append("    CASE WHEN COUNT(DISTINCT er.export_id) > 0 ");
        sql.append("         THEN ISNULL(SUM(ed.amount), 0) / COUNT(DISTINCT er.export_id) ");
        sql.append("         ELSE 0 END AS avg_order_value ");
        sql.append("FROM ExportReceipts er ");
        sql.append("INNER JOIN ExportDetails ed ON er.export_id = ed.export_id ");
        sql.append("INNER JOIN Products p ON ed.product_id = p.product_id ");
        sql.append("INNER JOIN Categories c ON p.category_id = c.category_id ");
        sql.append("WHERE er.date >= ? AND er.date <= ? ");
        
        if (productId != null) {
            sql.append("AND p.product_id = ? ");
        }
        if (categoryId != null) {
            sql.append("AND c.category_id = ? ");
        }
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql.toString());
            st.setQueryTimeout(30);
            
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
            
            int paramIndex = 1;
            st.setDate(paramIndex++, sqlStartDate);
            st.setDate(paramIndex++, sqlEndDate);
            
            if (productId != null) {
                st.setInt(paramIndex++, productId);
            }
            if (categoryId != null) {
                st.setInt(paramIndex++, categoryId);
            }
            
            rs = st.executeQuery();
            
            if (rs.next()) {
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                int totalQuantity = rs.getInt("total_quantity");
                int totalReceipts = rs.getInt("total_receipts");
                BigDecimal avgOrderValue = rs.getBigDecimal("avg_order_value");
                
                // Get top product revenue
                BigDecimal topProductRevenue = getTopProductRevenue(startDate, endDate, productId, categoryId);
                
                return new Object[]{totalRevenue, totalQuantity, totalReceipts, avgOrderValue, topProductRevenue};
            }
        } catch (SQLException e) {
            System.out.println("Error in getRevenueSummaryStatistics: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return new Object[]{BigDecimal.ZERO, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO};
    }

    /**
     * Get top product revenue for summary
     */
    private BigDecimal getTopProductRevenue(Date startDate, Date endDate, Integer productId, Integer categoryId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 ISNULL(SUM(ed.amount), 0) AS top_revenue ");
        sql.append("FROM ExportReceipts er ");
        sql.append("INNER JOIN ExportDetails ed ON er.export_id = ed.export_id ");
        sql.append("INNER JOIN Products p ON ed.product_id = p.product_id ");
        sql.append("INNER JOIN Categories c ON p.category_id = c.category_id ");
        sql.append("WHERE er.date >= ? AND er.date <= ? ");
        
        if (productId != null) {
            sql.append("AND p.product_id = ? ");
        }
        if (categoryId != null) {
            sql.append("AND c.category_id = ? ");
        }
        
        sql.append("GROUP BY p.product_id, p.name ");
        sql.append("ORDER BY SUM(ed.amount) DESC ");
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql.toString());
            st.setQueryTimeout(30);
            
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
            
            int paramIndex = 1;
            st.setDate(paramIndex++, sqlStartDate);
            st.setDate(paramIndex++, sqlEndDate);
            
            if (productId != null) {
                st.setInt(paramIndex++, productId);
            }
            if (categoryId != null) {
                st.setInt(paramIndex++, categoryId);
            }
            
            rs = st.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("top_revenue");
            }
        } catch (SQLException e) {
            System.out.println("Error in getTopProductRevenue: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Get comprehensive report data for export functionality
     * @param reportType Type of report: "inventory", "import-export", "revenue", "comprehensive"
     * @param startDate Start date (null for 30 days ago)
     * @param endDate End date (null for today)
     * @param productId Optional product filter (null for all products)
     * @param categoryId Optional category filter (null for all categories)
     * @return List of export report data
     */
    public List<ExportReportDTO> getComprehensiveReportData(String reportType, Date startDate, Date endDate,
                                                           Integer productId, Integer categoryId) {
        List<ExportReportDTO> reports = new ArrayList<>();

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

        String sql = buildExportReportQuery(reportType);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int paramIndex = 1;

            // Set date parameters
            ps.setString(paramIndex++, sdf.format(startDate));
            ps.setString(paramIndex++, sdf.format(endDate));

            // For non-inventory reports, set additional date parameters for import/export data
            if (!"inventory".equals(reportType)) {
                ps.setString(paramIndex++, sdf.format(startDate));
                ps.setString(paramIndex++, sdf.format(endDate));
                ps.setString(paramIndex++, sdf.format(startDate));
                ps.setString(paramIndex++, sdf.format(endDate));
                ps.setString(paramIndex++, reportType); // For comprehensive filter
            }

            // Set optional filters (these would need to be added to the WHERE clause)
            if (productId != null) {
                // ps.setInt(paramIndex++, productId);
            }
            if (categoryId != null) {
                // ps.setInt(paramIndex++, categoryId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExportReportDTO report = new ExportReportDTO();

                    // Basic information
                    report.setReportDate(rs.getDate("report_date"));
                    report.setDateLabel(rs.getString("date_label"));
                    report.setReportPeriod(sdf.format(startDate) + " to " + sdf.format(endDate));

                    // Product information
                    report.setProductId(rs.getInt("product_id"));
                    report.setProductCode(rs.getString("product_code"));
                    report.setProductName(rs.getString("product_name"));
                    report.setUnit(rs.getString("unit"));
                    report.setCategoryId(rs.getInt("category_id"));
                    report.setCategoryName(rs.getString("category_name"));

                    // Supplier information
                    report.setSupplierId(rs.getInt("supplier_id"));
                    report.setSupplierName(rs.getString("supplier_name"));

                    // Current inventory
                    report.setCurrentStock(rs.getInt("current_stock"));
                    report.setImportPrice(rs.getBigDecimal("import_price"));
                    report.setExportPrice(rs.getBigDecimal("export_price"));

                    // Import/Export data
                    report.setImportQuantity(rs.getInt("import_quantity"));
                    report.setExportQuantity(rs.getInt("export_quantity"));
                    report.setImportValue(rs.getBigDecimal("import_value"));
                    report.setExportValue(rs.getBigDecimal("export_value"));
                    report.setImportReceiptCount(rs.getInt("import_receipt_count"));
                    report.setExportReceiptCount(rs.getInt("export_receipt_count"));
                    report.setAverageImportValue(rs.getBigDecimal("avg_import_value"));
                    report.setAverageExportValue(rs.getBigDecimal("avg_export_value"));

                    // Calculate derived fields
                    report.calculateStockValue();
                    report.calculateStockMovement();
                    report.calculateProfitMargin();
                    report.calculateProfitPercentage();
                    report.calculateTurnoverRatio();

                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    /**
     * Build SQL query based on report type
     */
    private String buildExportReportQuery(String reportType) {
        String baseQuery = """
            WITH DateSeries AS (
                SELECT CAST(? AS DATE) AS report_date
                UNION ALL
                SELECT DATEADD(day, 1, report_date)
                FROM DateSeries
                WHERE report_date < CAST(? AS DATE)
            ),
            ProductData AS (
                SELECT
                    p.product_id as product_id,
                    p.code as product_code,
                    p.name as product_name,
                    p.unit as unit,
                    p.quantity as current_stock,
                    p.import_price as import_price,
                    p.export_price as export_price,
                    c.category_id as category_id,
                    c.name as category_name,
                    NULL as supplier_id,
                    'N/A' as supplier_name
                FROM Products p
                LEFT JOIN Categories c ON p.category_id = c.category_id
                WHERE 1=1
            """;

        if (reportType.equals("inventory")) {
            return baseQuery + """
                )
                SELECT
                    GETDATE() as report_date,
                    'Current Inventory' as date_label,
                    pd.*,
                    0 as import_quantity,
                    0 as export_quantity,
                    0.0 as import_value,
                    0.0 as export_value,
                    0 as import_receipt_count,
                    0 as export_receipt_count,
                    0.0 as avg_import_value,
                    0.0 as avg_export_value
                FROM ProductData pd
                ORDER BY pd.category_name, pd.product_name
                """;
        } else {
            return baseQuery + """
                ),
                ImportData AS (
                    SELECT
                        CAST(ir.date AS DATE) as import_date,
                        id.product_id,
                        SUM(id.quantity) as import_quantity,
                        SUM(id.quantity * id.price) as import_value,
                        COUNT(DISTINCT ir.import_id) as import_receipt_count,
                        AVG(id.quantity * id.price) as avg_import_value
                    FROM ImportReceipts ir
                    JOIN ImportDetails id ON ir.import_id = id.import_id
                    WHERE ir.date >= ? AND ir.date <= ?
                    GROUP BY CAST(ir.date AS DATE), id.product_id
                ),
                ExportData AS (
                    SELECT
                        CAST(er.date AS DATE) as export_date,
                        ed.product_id,
                        SUM(ed.quantity) as export_quantity,
                        SUM(ed.quantity * ed.price) as export_value,
                        COUNT(DISTINCT er.export_id) as export_receipt_count,
                        AVG(ed.quantity * ed.price) as avg_export_value
                    FROM ExportReceipts er
                    JOIN ExportDetails ed ON er.export_id = ed.export_id
                    WHERE er.date >= ? AND er.date <= ?
                    GROUP BY CAST(er.date AS DATE), ed.product_id
                )
                SELECT
                    ds.report_date,
                    FORMAT(ds.report_date, 'yyyy-MM-dd') as date_label,
                    pd.*,
                    ISNULL(imp.import_quantity, 0) as import_quantity,
                    ISNULL(exp.export_quantity, 0) as export_quantity,
                    ISNULL(imp.import_value, 0.0) as import_value,
                    ISNULL(exp.export_value, 0.0) as export_value,
                    ISNULL(imp.import_receipt_count, 0) as import_receipt_count,
                    ISNULL(exp.export_receipt_count, 0) as export_receipt_count,
                    ISNULL(imp.avg_import_value, 0.0) as avg_import_value,
                    ISNULL(exp.avg_export_value, 0.0) as avg_export_value
                FROM DateSeries ds
                CROSS JOIN ProductData pd
                LEFT JOIN ImportData imp ON ds.report_date = imp.import_date AND pd.product_id = imp.product_id
                LEFT JOIN ExportData exp ON ds.report_date = exp.export_date AND pd.product_id = exp.product_id
                WHERE (imp.import_quantity > 0 OR exp.export_quantity > 0 OR ? = 'comprehensive')
                ORDER BY ds.report_date, pd.category_name, pd.product_name
                OPTION (MAXRECURSION 0)
                """;
        }
    }
}

