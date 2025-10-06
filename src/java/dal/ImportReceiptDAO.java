package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import model.ImportDetail;
import model.ImportReceipt;

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

    public void createReceiptWithDetails(ImportReceipt receipt) throws SQLException {
        try {
            connection.setAutoCommit(false);
            int importId = createImportReceipt(receipt);
            
            // Share the same connection with child DAOs for transaction consistency
            ImportDetailDAO importDetailDAO = new ImportDetailDAO(connection);
            ProductDAO productDAO = new ProductDAO(connection);
            
            for (ImportDetail d : receipt.getDetails()) {
                // Map product code to id if needed
                if (d.getProductId() == 0 && d.getProductCode() != null) {
                    Integer pId = productDAO.getProductIdByCode(d.getProductCode());
                    if (pId == null) {
                        // Create new product if not existing
                        pId = productDAO.createProduct(d.getProductCode(), d.getProductName(), d.getPrice(), d.getCategoryId());
                    }
                    d.setProductId(pId);
                }
                importDetailDAO.insertImportDetail(d, importId);
                productDAO.increaseProductStock(d.getProductId(), d.getQuantity(), d.getPrice());
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


