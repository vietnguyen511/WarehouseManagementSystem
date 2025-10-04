package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import model.ExportDetail;
import model.ExportReceipt;

public class ExportReceiptDAO extends DBContext {

    // Constructor for standalone use
    public ExportReceiptDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public ExportReceiptDAO(Connection connection) {
        super(connection);
    }

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
            return rs.getInt(1);
        }
        return -1;
    }

    /**
     * Create export receipt with details and update stock automatically
     * Uses transaction to ensure data consistency
     */
    public void createReceiptWithDetails(ExportReceipt receipt) throws SQLException {
        try {
            connection.setAutoCommit(false);
            int exportId = createExportReceipt(receipt);
            
            // Share the same connection with child DAOs for transaction consistency
            ExportDetailDAO exportDetailDAO = new ExportDetailDAO(connection);
            ProductDAO productDAO = new ProductDAO(connection);
            
            for (ExportDetail d : receipt.getDetails()) {
                // Validate stock before export
                Integer availableQty = productDAO.getProductQuantity(d.getProductId());
                if (availableQty == null || availableQty < d.getQuantity()) {
                    throw new SQLException("Insufficient stock for product_id: " + d.getProductId() 
                        + ". Available: " + (availableQty != null ? availableQty : 0) 
                        + ", Required: " + d.getQuantity());
                }
                
                // Insert export detail
                exportDetailDAO.insertExportDetail(d, exportId);
                
                // Decrease stock automatically
                productDAO.decreaseProductStock(d.getProductId(), d.getQuantity());
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}

