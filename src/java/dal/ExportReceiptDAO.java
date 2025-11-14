package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ExportDetail;
import model.ExportReceipt;
import model.ExportReceiptListDTO;
import model.ProductVariant;

/**
 * ExportReceiptDAO - Data access object for ExportReceipts and related operations.
 * Modified createReceiptWithDetails to insert details transactionally and update variant/product stock.
 */
public class ExportReceiptDAO extends DBContext {

    public int createExportReceipt(ExportReceipt receipt) throws SQLException {
        String sql = "INSERT INTO ExportReceipts (customer_id, user_id, date, total_quantity, total_amount, note, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        st.setInt(1, receipt.getCustomerId());
        st.setInt(2, receipt.getUserId());
        st.setTimestamp(3, new java.sql.Timestamp(receipt.getDate().getTime()));
        st.setInt(4, receipt.getTotalQuantity());
        st.setBigDecimal(5, receipt.getTotalAmount() != null ? receipt.getTotalAmount() : BigDecimal.ZERO);
        st.setString(6, receipt.getNote());
        st.executeUpdate();
        ResultSet rs = st.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            try { rs.close(); } catch (Exception ignore) {}
            try { st.close(); } catch (Exception ignore) {}
            return id;
        }
        try { rs.close(); } catch (Exception ignore) {}
        try { st.close(); } catch (Exception ignore) {}
        return -1;
    }

    // Import detail operations moved to ImportDetailDAO

    // Product-related operations moved to ProductDAO

   public void decreaseProductStock(int productId, int quantity, BigDecimal newExportPrice) throws SQLException {
    // Chỉ cập nhật nếu quantity hiện tại >= quantity yêu cầu
    String sql = "UPDATE Products SET quantity = quantity - ?, export_price = ISNULL(?, export_price), updated_at = GETDATE() WHERE product_id = ? AND quantity >= ?";
    PreparedStatement st = connection.prepareStatement(sql);
    st.setInt(1, quantity);
    st.setBigDecimal(2, newExportPrice);
    st.setInt(3, productId);
    st.setInt(4, quantity);
    int affected = st.executeUpdate();
    try { st.close(); } catch (Exception ignore) {}

    if (affected == 0) {
        // Không update được: có thể product không tồn tại hoặc không đủ tồn kho
        throw new SQLException("Failed to decrease stock: product_id=" + productId + " (not found or insufficient stock).");
    }
}


    /**
     * Create export receipt (header) and export details in one transaction.
     * For each ExportDetail:
     *  - try find variant by productCode/size/color
     *  - if not exists, try find product by code -> if not exists, create product -> create variant
     *  - insert export detail and update variant stock + product price
     *
     * This method prints DEBUG messages to console to help tracing.
     */
    public int createReceiptWithDetails(ExportReceipt receipt) throws SQLException {
        PreparedStatement psReceipt = null;
        ResultSet rsKeys = null;
        boolean prevAuto = true;
        try {
            // preserve previous auto-commit
            try { prevAuto = connection.getAutoCommit(); } catch (SQLException ignore) {}

            connection.setAutoCommit(false);

            String sqlReceipt = "INSERT INTO ExportReceipts (customer_id, user_id, date, total_quantity, total_amount, note, created_at) "
                              + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
            psReceipt = connection.prepareStatement(sqlReceipt, Statement.RETURN_GENERATED_KEYS);
            psReceipt.setInt(1, receipt.getCustomerId());
            psReceipt.setInt(2, receipt.getUserId());
            psReceipt.setTimestamp(3, new java.sql.Timestamp(receipt.getDate().getTime()));
            psReceipt.setInt(4, receipt.getTotalQuantity());
            psReceipt.setBigDecimal(5, receipt.getTotalAmount() != null ? receipt.getTotalAmount() : BigDecimal.ZERO);
            psReceipt.setString(6, receipt.getNote());

            System.out.println("DEBUG: Inserting ExportReceipt (customer=" + receipt.getCustomerId()
                    + ", totalQty=" + receipt.getTotalQuantity()
                    + ", totalAmount=" + receipt.getTotalAmount() + ")");
            psReceipt.executeUpdate();

            rsKeys = psReceipt.getGeneratedKeys();
            int exportId = -1;
            if (rsKeys.next()) {
                exportId = rsKeys.getInt(1);
                System.out.println("DEBUG: Generated exportId = " + exportId);
            } else {
                throw new SQLException("Failed to obtain generated exportId.");
            }

            // DAOs sharing the same connection for transactional consistency
            ExportDetailDAO exportDetailDAO = new ExportDetailDAO(connection);
            ProductVariantDAO variantDAO = new ProductVariantDAO(connection);
            ProductDAO productDAO = new ProductDAO(connection);

            if (receipt.getDetails() != null) {
                for (ExportDetail d : receipt.getDetails()) {
                    System.out.println("DEBUG: Processing detail: productCode=" + d.getProductCode()
                            + " size=" + d.getSize() + " color=" + d.getColor()
                            + " qty=" + d.getQuantity() + " price=" + d.getPrice());

                    ProductVariant variant = null;
                    try {
                        // try find existing variant by product code + size + color
                        variant = variantDAO.getVariantByProductCodeSizeColor(d.getProductCode(), d.getSize(), d.getColor());
                    } catch (Exception ex) {
                        // Log and continue (variant lookup may fail if method signature differs)
                        System.out.println("WARN: variant lookup threw exception for code=" + d.getProductCode());
                        ex.printStackTrace();
                        variant = null;
                    }

                    if (variant == null) {
                        // try find product by code
                        Integer productId = null;
                        try {
                            productId = productDAO.getProductIdByCode(d.getProductCode());
                        } catch (Exception ex) {
                            System.out.println("WARN: product lookup threw exception for code=" + d.getProductCode());
                            ex.printStackTrace();
                            productId = null;
                        }                     

                        if (productId != null) {
                            try {
                                // create variant
                                ProductVariant newVar = new ProductVariant();
                                newVar.setProductId(productId);
                                newVar.setSize(d.getSize());
                                newVar.setColor(d.getColor());
                                newVar.setQuantity(0);
                                newVar.setStatus(true);
                                int variantId = variantDAO.createProductVariant(newVar);
                                newVar.setVariantId(variantId);
                                variant = newVar;
                                System.out.println("DEBUG: Created variant id=" + variantId + " for productId=" + productId);
                            } catch (Exception ex) {
                                System.out.println("WARN: failed to create variant for productId=" + productId);
                                ex.printStackTrace();
                                variant = null;
                            }
                        }
                    }

                    // If we have a variant, set variantId on detail; otherwise leave as 0 (insertExportDetail will write NULL)
                    if (variant != null) {
                        d.setVariantId(variant.getVariantId());
                    } else {
                        d.setVariantId(0); // insertExportDetail handles 0 -> NULL
                    }

                    // Insert detail
                    try {
                        exportDetailDAO.insertExportDetail(d, exportId);
                        System.out.println("DEBUG: Inserted ExportDetail for exportId=" + exportId + " productCode=" + d.getProductCode());
                    } catch (Exception ex) {
                        System.err.println("ERROR: Failed to insert ExportDetail for productCode=" + d.getProductCode());
                        ex.printStackTrace();
                        throw ex; // will be caught by outer catch and rollback
                    }

                    // Update variant stock and product price if we have variant info
                    // Update variant/product stock for export (decrease)
                    try {
                       if (variant != null && variant.getVariantId() > 0) {
                           variantDAO.decreaseVariantStock(variant.getVariantId(), d.getQuantity());
                           if (variant.getProductId() > 0) {
                               productDAO.updateQuantityAsSumOfVariants(variant.getProductId());
                               }
                            System.out.println("DEBUG: Decreased variant stock for variantId=" + variant.getVariantId() + " by -" + d.getQuantity());
                        } else {

                        Integer productId = productDAO.getProductIdByCode(d.getProductCode());
                        if (productId != null) {
                             productDAO.decreaseProductStock(productId, d.getQuantity());
                             System.out.println("DEBUG: Decreased product stock for productId=" + productId + " by -" + d.getQuantity());
                        } else {
                        System.out.println("WARN: cannot find product to decrease stock for code=" + d.getProductCode());
                        }
                    }
                } catch (SQLException ex) {
                        System.err.println("ERROR: Failed to decrease stock for productCode=" + d.getProductCode());
                        ex.printStackTrace();
                    throw ex;
                    }

                } // end for each detail
            } // end if details != null

            connection.commit();
            System.out.println("DEBUG: Transaction committed for exportId=" + exportId);
            return exportId;
        } catch (SQLException e) {
            try {
                connection.rollback();
                System.err.println("DEBUG: Transaction rolled back");
            } catch (SQLException ignore) {}
            throw e;
        } finally {
            try { connection.setAutoCommit(prevAuto); } catch (SQLException ignore) {}
            if (rsKeys != null) try { rsKeys.close(); } catch (Exception ignore) {}
            if (psReceipt != null) try { psReceipt.close(); } catch (Exception ignore) {}
        }
    }

    public List<ExportReceiptListDTO> getAllExportReceipts() throws SQLException {
        return getAllExportReceipts(0, Integer.MAX_VALUE);
    }

    public List<ExportReceiptListDTO> getAllExportReceipts(int offset, int limit) throws SQLException {
        return getAllExportReceipts(offset, limit, null, null);
    }

    public List<ExportReceiptListDTO> getAllExportReceipts(int offset, int limit, String searchTerm, String dateFilter) throws SQLException {
        List<ExportReceiptListDTO> receipts = new ArrayList<>();
        
        // Build dynamic SQL with search and filter conditions
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT er.export_id, er.date, c.name as customer_name, ");
        sql.append("er.total_quantity, er.total_amount, u.fullname as user_name, er.note ");
        sql.append("FROM ExportReceipts er ");
        sql.append("INNER JOIN Customers c ON er.customer_id = c.customer_id ");
        sql.append("INNER JOIN Users u ON er.user_id = u.user_id ");
        
        // Add WHERE conditions
        boolean hasWhere = false;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("WHERE (");
            sql.append("CAST(er.export_id AS VARCHAR) LIKE ? OR ");
            sql.append("c.name LIKE ? OR ");
            sql.append("u.fullname LIKE ? OR ");
            sql.append("er.note LIKE ?");
            sql.append(") ");
            hasWhere = true;
        }
        
        if (dateFilter != null && !dateFilter.trim().isEmpty()) {
            if (hasWhere) {
                sql.append("AND ");
            } else {
                sql.append("WHERE ");
                hasWhere = true;
            }
            
            switch (dateFilter.toLowerCase()) {
                case "today":
                    sql.append("CAST(er.date AS DATE) = CAST(GETDATE() AS DATE) ");
                    break;
                case "week":
                    sql.append("er.date >= DATEADD(DAY, -7, GETDATE()) ");
                    break;
                case "month":
                    sql.append("YEAR(er.date) = YEAR(GETDATE()) AND MONTH(er.date) = MONTH(GETDATE()) ");
                    break;
                case "quarter":
                    sql.append("er.date >= DATEADD(MONTH, -3, GETDATE()) ");
                    break;
                default:
                    // Remove the AND/WHERE if invalid filter
                    sql.setLength(sql.length() - (hasWhere && searchTerm != null ? 4 : 5));
                    hasWhere = false;
                    break;
            }
        }
        
        sql.append("ORDER BY er.export_id ASC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        PreparedStatement st = connection.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        // Set search parameters
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchPattern = "%" + searchTerm.trim() + "%";
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
        }
        
        // Set pagination parameters
        st.setInt(paramIndex++, offset);
        st.setInt(paramIndex++, limit);
        
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            ExportReceiptListDTO dto = new ExportReceiptListDTO();
            dto.setExportId(rs.getInt("export_id"));
            dto.setDate(rs.getTimestamp("date"));
            dto.setCustomerName(rs.getString("customer_name"));
            dto.setTotalQuantity(rs.getInt("total_quantity"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setUserName(rs.getString("user_name"));
            dto.setNote(rs.getString("note"));
            receipts.add(dto);
        }
        
        try { rs.close(); } catch (Exception ignore) {}
        try { st.close(); } catch (Exception ignore) {}
        return receipts;
    }

    public int getTotalExportReceiptsCount() throws SQLException {
        return getTotalExportReceiptsCount(null, null);
    }

    public int getTotalExportReceiptsCount(String searchTerm, String dateFilter) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ExportReceipts er ");
        sql.append("INNER JOIN Customers c ON er.customer_id = c.customer_id ");
        sql.append("INNER JOIN Users u ON er.user_id = u.user_id ");
        
        // Add WHERE conditions
        boolean hasWhere = false;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("WHERE (");
            sql.append("CAST(er.export_id AS VARCHAR) LIKE ? OR ");
            sql.append("c.name LIKE ? OR ");
            sql.append("u.fullname LIKE ? OR ");
            sql.append("er.note LIKE ?");
            sql.append(") ");
            hasWhere = true;
        }
        
        if (dateFilter != null && !dateFilter.trim().isEmpty()) {
            if (hasWhere) {
                sql.append("AND ");
            } else {
                sql.append("WHERE ");
                hasWhere = true;
            }
            
            switch (dateFilter.toLowerCase()) {
                case "today":
                    sql.append("CAST(er.date AS DATE) = CAST(GETDATE() AS DATE) ");
                    break;
                case "week":
                    sql.append("er.date >= DATEADD(DAY, -7, GETDATE()) ");
                    break;
                case "month":
                    sql.append("YEAR(er.date) = YEAR(GETDATE()) AND MONTH(er.date) = MONTH(GETDATE()) ");
                    break;
                case "quarter":
                    sql.append("er.date >= DATEADD(MONTH, -3, GETDATE()) ");
                    break;
                default:
                    // Remove the AND/WHERE if invalid filter
                    sql.setLength(sql.length() - (hasWhere && searchTerm != null ? 4 : 5));
                    hasWhere = false;
                    break;
            }
        }
        
        PreparedStatement st = connection.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        // Set search parameters
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String searchPattern = "%" + searchTerm.trim() + "%";
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
            st.setString(paramIndex++, searchPattern);
        }
        
        ResultSet rs = st.executeQuery();
        
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        try { rs.close(); } catch (Exception ignore) {}
        try { st.close(); } catch (Exception ignore) {}
        return count;
    }

    public ExportReceipt getExportReceiptById(int exportId) throws SQLException {
        String sql = "SELECT er.export_id, er.customer_id, er.user_id, er.date, " +
                    "er.total_quantity, er.total_amount, er.note, " +
                    "c.name as customer_name, u.fullname as user_name " +
                    "FROM ExportReceipts er " +
                    "INNER JOIN Customers c ON er.customer_id = c.customer_id " +
                    "INNER JOIN Users u ON er.user_id = u.user_id " +
                    "WHERE er.export_id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, exportId);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            ExportReceipt receipt = new ExportReceipt();
            receipt.setExportId(rs.getInt("export_id"));
            receipt.setCustomerId(rs.getInt("customer_id"));
            receipt.setUserId(rs.getInt("user_id"));
            receipt.setDate(rs.getTimestamp("date"));
            receipt.setTotalQuantity(rs.getInt("total_quantity"));
            receipt.setTotalAmount(rs.getBigDecimal("total_amount"));
            receipt.setNote(rs.getString("note"));
            receipt.setCustomerName(rs.getString("customer_name"));
            receipt.setUserName(rs.getString("user_name"));
            try { rs.close(); } catch (Exception ignore) {}
            try { st.close(); } catch (Exception ignore) {}
            return receipt;
        }
        
        try { rs.close(); } catch (Exception ignore) {}
        try { st.close(); } catch (Exception ignore) {}
        return null;
    }
    
    /**
     * Get export statistics grouped by day or month
     * @param startDate Start date
     * @param endDate End date
     * @param groupBy "day" or "month"
     * @return List of ExportStatDTO
     */
    public List<model.ExportStatDTO> getExportStatistics(java.util.Date startDate, java.util.Date endDate, String groupBy) throws SQLException {
        List<model.ExportStatDTO> stats = new ArrayList<>();
        
        // Determine grouping
        boolean isMonthly = "month".equalsIgnoreCase(groupBy);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if (isMonthly) {
            sql.append("    FORMAT(er.date, 'yyyy-MM') AS period, ");
        } else {
            sql.append("    CAST(er.date AS DATE) AS period, ");
        }
        sql.append("    COUNT(DISTINCT er.export_id) AS receipt_count, ");
        sql.append("    ISNULL(SUM(ed.quantity), 0) AS total_quantity, ");
        sql.append("    ISNULL(SUM(ed.amount), 0) AS total_amount ");
        sql.append("FROM ExportReceipts er ");
        sql.append("LEFT JOIN ExportDetails ed ON er.export_id = ed.export_id ");
        sql.append("WHERE er.date >= ? AND er.date <= ? ");
        sql.append("GROUP BY ");
        if (isMonthly) {
            sql.append("    FORMAT(er.date, 'yyyy-MM') ");
        } else {
            sql.append("    CAST(er.date AS DATE) ");
        }
        sql.append("ORDER BY period ASC");
        
        PreparedStatement st = connection.prepareStatement(sql.toString());
        st.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
        st.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            model.ExportStatDTO stat = new model.ExportStatDTO();
            stat.setPeriod(rs.getString("period"));
            stat.setReceiptCount(rs.getInt("receipt_count"));
            stat.setTotalQuantity(rs.getInt("total_quantity"));
            stat.setTotalAmount(rs.getBigDecimal("total_amount"));
            stats.add(stat);
        }
        
        try { rs.close(); } catch (Exception ignore) {}
        try { st.close(); } catch (Exception ignore) {}
        return stats;
    }
    
    /**
     * Get total export statistics (aggregated)
     * Returns an array: [totalReceipts, totalQuantity, totalAmount, avgAmount]
     */
    public Object[] getTotalExportStatistics(java.util.Date startDate, java.util.Date endDate) throws SQLException {
        String sql = "SELECT " +
                    "COUNT(DISTINCT er.export_id) AS total_receipts, " +
                    "ISNULL(SUM(ed.quantity), 0) AS total_quantity, " +
                    "ISNULL(SUM(ed.amount), 0) AS total_amount, " +
                    "ISNULL(AVG(er.total_amount), 0) AS avg_amount " +
                    "FROM ExportReceipts er " +
                    "LEFT JOIN ExportDetails ed ON er.export_id = ed.export_id " +
                    "WHERE er.date >= ? AND er.date <= ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
        st.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            int totalReceipts = rs.getInt("total_receipts");
            int totalQuantity = rs.getInt("total_quantity");
            java.math.BigDecimal totalAmount = rs.getBigDecimal("total_amount");
            java.math.BigDecimal avgAmount = rs.getBigDecimal("avg_amount");
            try { rs.close(); } catch (Exception ignore) {}
            try { st.close(); } catch (Exception ignore) {}
            return new Object[]{totalReceipts, totalQuantity, totalAmount, avgAmount};
        }
        
        try { rs.close(); } catch (Exception ignore) {}
        try { st.close(); } catch (Exception ignore) {}
        return new Object[]{0, 0, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO};
    }
}
