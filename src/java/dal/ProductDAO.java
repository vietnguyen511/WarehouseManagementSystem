package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductDAO extends DBContext {

    // Constructor for standalone use
    public ProductDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public ProductDAO(Connection connection) {
        super(connection);
    }

    public Integer getProductIdByCode(String code) throws SQLException {
        String sql = "SELECT product_id FROM Products WHERE code = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, code);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return null;
    }

    public int createProduct(String code, String name, BigDecimal importPrice, Integer categoryId) throws SQLException {
        String sql = "INSERT INTO Products (code, name, category_id, unit, quantity, import_price, status, created_at, updated_at) "
                   + "VALUES (?, ?, ?, 'piece', 0, ?, 1, GETDATE(), GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        st.setString(1, code);
        st.setString(2, name != null ? name : code);
        if (categoryId != null) {
            st.setInt(3, categoryId);
        } else {
            st.setNull(3, java.sql.Types.INTEGER);
        }
        st.setBigDecimal(4, importPrice);
        st.executeUpdate();
        ResultSet rs = st.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        throw new SQLException("Failed to create product for code: " + code);
    }
    
    // Overloaded method for backward compatibility
    public int createProduct(String code, String name, BigDecimal importPrice) throws SQLException {
        return createProduct(code, name, importPrice, null);
    }

    public void increaseProductStock(int productId, int quantity, BigDecimal newImportPrice) throws SQLException {
        String sql = "UPDATE Products SET quantity = quantity + ?, import_price = ISNULL(?, import_price), updated_at = GETDATE() WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, quantity);
        st.setBigDecimal(2, newImportPrice);
        st.setInt(3, productId);
        st.executeUpdate();
    }

    public void decreaseProductStock(int productId, int quantity) throws SQLException {
        String sql = "UPDATE Products SET quantity = quantity - ?, updated_at = GETDATE() WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, quantity);
        st.setInt(2, productId);
        int rowsAffected = st.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("Failed to decrease stock for product_id: " + productId);
        }
    }

    public Integer getProductQuantity(int productId) throws SQLException {
        String sql = "SELECT quantity FROM Products WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, productId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt("quantity");
        }
        return null;
    }

    public model.Product getProductByCode(String code) throws SQLException {
        String sql = "SELECT TOP 1 p.product_id, p.code, p.name, p.unit, p.quantity, p.import_price, p.export_price, p.status, p.category_id, c.name AS category_name " +
                     "FROM Products p " +
                     "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                     "WHERE p.code = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, code);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            model.Product p = new model.Product();
            p.setProductId(rs.getInt("product_id"));
            p.setCode(rs.getString("code"));
            p.setName(rs.getString("name"));
            p.setUnit(rs.getString("unit"));
            p.setQuantity(rs.getInt("quantity"));
            p.setImportPrice(rs.getBigDecimal("import_price"));
            p.setExportPrice(rs.getBigDecimal("export_price"));
            p.setStatus(rs.getBoolean("status"));
            // Get category info
            int categoryId = rs.getInt("category_id");
            p.setCategoryId(rs.wasNull() ? null : categoryId);
            p.setCategoryName(rs.getString("category_name"));
            return p;
        }
        return null;
    }
}


