package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ImportDetail;
import model.ImportReceipt;
import model.ImportReceiptListDTO;
import model.ProductVariant;

public class ImportReceiptDAO extends DBContext {

    public int createImportReceipt(ImportReceipt receipt) throws SQLException {
        String sql = "INSERT INTO ImportReceipts (supplier_id, user_id, date, total_quantity, total_amount, note, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        st.setInt(1, receipt.getSupplierId());
        st.setInt(2, receipt.getUserId());
        st.setTimestamp(3, new java.sql.Timestamp(receipt.getDate().getTime()));
        st.setInt(4, receipt.getTotalQuantity());
        st.setBigDecimal(5, receipt.getTotalAmount() != null ? receipt.getTotalAmount() : BigDecimal.ZERO);
        st.setString(6, receipt.getNote());
        st.executeUpdate();
        ResultSet rs = st.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    // Import detail operations moved to ImportDetailDAO

    // Product-related operations moved to ProductDAO

    public void increaseProductStock(int productId, int quantity, BigDecimal newImportPrice) throws SQLException {
        String sql = "UPDATE Products SET quantity = quantity + ?, import_price = ISNULL(?, import_price), updated_at = GETDATE() WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, quantity);
        st.setBigDecimal(2, newImportPrice);
        st.setInt(3, productId);
        st.executeUpdate();
    }

    public int createReceiptWithDetails(ImportReceipt receipt) throws SQLException {
        try {
            connection.setAutoCommit(false);
            int importId = createImportReceipt(receipt);
            
            // Share the same connection with child DAOs for transaction consistency
            ImportDetailDAO importDetailDAO = new ImportDetailDAO(connection);
            ProductVariantDAO variantDAO = new ProductVariantDAO(connection);
            ProductDAO productDAO = new ProductDAO(connection);
            
            for (ImportDetail d : receipt.getDetails()) {
                // Find or create product variant
                ProductVariant variant = variantDAO.getVariantByProductCodeSizeColor(
                    d.getProductCode(), d.getSize(), d.getColor());
                
                if (variant == null) {
                    // Create new product if not existing
                    Integer productId = productDAO.getProductIdByCode(d.getProductCode());
                    if (productId == null) {
                        productId = productDAO.createProduct(d.getProductCode(), d.getProductName(), d.getMaterial(), d.getUnit(), d.getPrice(), d.getCategoryId());
                    }
                    
                    // Create new variant
                    variant = new ProductVariant();
                    variant.setProductId(productId);
                    variant.setSize(d.getSize());
                    variant.setColor(d.getColor());
                    variant.setQuantity(0);
                    variant.setStatus(true);
                    int variantId = variantDAO.createProductVariant(variant);
                    variant.setVariantId(variantId);
                }
                
                d.setVariantId(variant.getVariantId());
                importDetailDAO.insertImportDetail(d, importId);
                variantDAO.increaseVariantStock(variant.getVariantId(), d.getQuantity());
                // Cập nhật lại tổng quantity cho bảng product
                productDAO.updateQuantityAsSumOfVariants(variant.getProductId());
                
                // Update product's import_price with new price (from this import receipt)
                productDAO.updateImportPrice(variant.getProductId(), d.getPrice());
            }
            connection.commit();
            return importId;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<ImportReceiptListDTO> getAllImportReceipts() throws SQLException {
        return getAllImportReceipts(0, Integer.MAX_VALUE);
    }

    public List<ImportReceiptListDTO> getAllImportReceipts(int offset, int limit) throws SQLException {
        return getAllImportReceipts(offset, limit, null, null);
    }

    public List<ImportReceiptListDTO> getAllImportReceipts(int offset, int limit, String searchTerm, String dateFilter) throws SQLException {
        List<ImportReceiptListDTO> receipts = new ArrayList<>();
        
        // Build dynamic SQL with search and filter conditions
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ir.import_id, ir.date, s.name as supplier_name, ");
        sql.append("ir.total_quantity, ir.total_amount, u.fullname as user_name, ir.note ");
        sql.append("FROM ImportReceipts ir ");
        sql.append("INNER JOIN Suppliers s ON ir.supplier_id = s.supplier_id ");
        sql.append("INNER JOIN Users u ON ir.user_id = u.user_id ");
        
        // Add WHERE conditions
        boolean hasWhere = false;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("WHERE (");
            sql.append("CAST(ir.import_id AS VARCHAR) LIKE ? OR ");
            sql.append("s.name LIKE ? OR ");
            sql.append("u.fullname LIKE ? OR ");
            sql.append("ir.note LIKE ?");
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
                    sql.append("CAST(ir.date AS DATE) = CAST(GETDATE() AS DATE) ");
                    break;
                case "week":
                    sql.append("ir.date >= DATEADD(DAY, -7, GETDATE()) ");
                    break;
                case "month":
                    sql.append("YEAR(ir.date) = YEAR(GETDATE()) AND MONTH(ir.date) = MONTH(GETDATE()) ");
                    break;
                case "quarter":
                    sql.append("ir.date >= DATEADD(MONTH, -3, GETDATE()) ");
                    break;
                default:
                    // Remove the AND/WHERE if invalid filter
                    sql.setLength(sql.length() - (hasWhere && searchTerm != null ? 4 : 5));
                    hasWhere = false;
                    break;
            }
        }
        
        sql.append("ORDER BY ir.import_id ASC ");
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
            ImportReceiptListDTO dto = new ImportReceiptListDTO();
            dto.setImportId(rs.getInt("import_id"));
            dto.setDate(rs.getTimestamp("date"));
            dto.setSupplierName(rs.getString("supplier_name"));
            dto.setTotalQuantity(rs.getInt("total_quantity"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setUserName(rs.getString("user_name"));
            dto.setNote(rs.getString("note"));
            receipts.add(dto);
        }
        
        return receipts;
    }

    public int getTotalImportReceiptsCount() throws SQLException {
        return getTotalImportReceiptsCount(null, null);
    }

    public int getTotalImportReceiptsCount(String searchTerm, String dateFilter) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ImportReceipts ir ");
        sql.append("INNER JOIN Suppliers s ON ir.supplier_id = s.supplier_id ");
        sql.append("INNER JOIN Users u ON ir.user_id = u.user_id ");
        
        // Add WHERE conditions
        boolean hasWhere = false;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("WHERE (");
            sql.append("CAST(ir.import_id AS VARCHAR) LIKE ? OR ");
            sql.append("s.name LIKE ? OR ");
            sql.append("u.fullname LIKE ? OR ");
            sql.append("ir.note LIKE ?");
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
                    sql.append("CAST(ir.date AS DATE) = CAST(GETDATE() AS DATE) ");
                    break;
                case "week":
                    sql.append("ir.date >= DATEADD(DAY, -7, GETDATE()) ");
                    break;
                case "month":
                    sql.append("YEAR(ir.date) = YEAR(GETDATE()) AND MONTH(ir.date) = MONTH(GETDATE()) ");
                    break;
                case "quarter":
                    sql.append("ir.date >= DATEADD(MONTH, -3, GETDATE()) ");
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
        
        if (rs.next()) {
            return rs.getInt(1);
        }
        
        return 0;
    }

    public ImportReceipt getImportReceiptById(int importId) throws SQLException {
        String sql = "SELECT ir.import_id, ir.supplier_id, ir.user_id, ir.date, " +
                    "ir.total_quantity, ir.total_amount, ir.note, " +
                    "s.name as supplier_name, u.fullname as user_name " +
                    "FROM ImportReceipts ir " +
                    "INNER JOIN Suppliers s ON ir.supplier_id = s.supplier_id " +
                    "INNER JOIN Users u ON ir.user_id = u.user_id " +
                    "WHERE ir.import_id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, importId);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            ImportReceipt receipt = new ImportReceipt();
            receipt.setImportId(rs.getInt("import_id"));
            receipt.setSupplierId(rs.getInt("supplier_id"));
            receipt.setUserId(rs.getInt("user_id"));
            receipt.setDate(rs.getTimestamp("date"));
            receipt.setTotalQuantity(rs.getInt("total_quantity"));
            receipt.setTotalAmount(rs.getBigDecimal("total_amount"));
            receipt.setNote(rs.getString("note"));
            receipt.setSupplierName(rs.getString("supplier_name"));
            receipt.setUserName(rs.getString("user_name"));
            return receipt;
        }
        
        return null;
    }
    
    /**
     * Get import statistics grouped by day or month
     * @param startDate Start date
     * @param endDate End date
     * @param groupBy "day" or "month"
     * @return List of ImportStatDTO
     */
    public List<model.ImportStatDTO> getImportStatistics(java.util.Date startDate, java.util.Date endDate, String groupBy) throws SQLException {
        List<model.ImportStatDTO> stats = new ArrayList<>();
        
        // Determine grouping
        boolean isMonthly = "month".equalsIgnoreCase(groupBy);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if (isMonthly) {
            sql.append("    FORMAT(ir.date, 'yyyy-MM') AS period, ");
        } else {
            sql.append("    CAST(ir.date AS DATE) AS period, ");
        }
        sql.append("    COUNT(DISTINCT ir.import_id) AS receipt_count, ");
        sql.append("    ISNULL(SUM(id.quantity), 0) AS total_quantity, ");
        sql.append("    ISNULL(SUM(id.amount), 0) AS total_amount ");
        sql.append("FROM ImportReceipts ir ");
        sql.append("LEFT JOIN ImportDetails id ON ir.import_id = id.import_id ");
        sql.append("WHERE ir.date >= ? AND ir.date <= ? ");
        sql.append("GROUP BY ");
        if (isMonthly) {
            sql.append("    FORMAT(ir.date, 'yyyy-MM') ");
        } else {
            sql.append("    CAST(ir.date AS DATE) ");
        }
        sql.append("ORDER BY period ASC");
        
        PreparedStatement st = connection.prepareStatement(sql.toString());
        st.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
        st.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            model.ImportStatDTO stat = new model.ImportStatDTO();
            stat.setPeriod(rs.getString("period"));
            stat.setReceiptCount(rs.getInt("receipt_count"));
            stat.setTotalQuantity(rs.getInt("total_quantity"));
            stat.setTotalAmount(rs.getBigDecimal("total_amount"));
            stats.add(stat);
        }
        
        return stats;
    }
    
    /**
     * Get total import statistics (aggregated)
     * Returns an array: [totalReceipts, totalQuantity, totalAmount, avgAmount]
     */
    public Object[] getTotalImportStatistics(java.util.Date startDate, java.util.Date endDate) throws SQLException {
        String sql = "SELECT " +
                    "COUNT(DISTINCT ir.import_id) AS total_receipts, " +
                    "ISNULL(SUM(id.quantity), 0) AS total_quantity, " +
                    "ISNULL(SUM(id.amount), 0) AS total_amount, " +
                    "ISNULL(AVG(ir.total_amount), 0) AS avg_amount " +
                    "FROM ImportReceipts ir " +
                    "LEFT JOIN ImportDetails id ON ir.import_id = id.import_id " +
                    "WHERE ir.date >= ? AND ir.date <= ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
        st.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            int totalReceipts = rs.getInt("total_receipts");
            int totalQuantity = rs.getInt("total_quantity");
            java.math.BigDecimal totalAmount = rs.getBigDecimal("total_amount");
            java.math.BigDecimal avgAmount = rs.getBigDecimal("avg_amount");
            
            return new Object[]{totalReceipts, totalQuantity, totalAmount, avgAmount};
        }
        
        return new Object[]{0, 0, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO};
    }
}


