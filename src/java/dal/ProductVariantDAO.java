package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ProductVariant;

public class ProductVariantDAO extends DBContext {

    // Constructor for standalone use
    public ProductVariantDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public ProductVariantDAO(Connection connection) {
        super(connection);
    }

    public int createProductVariant(ProductVariant variant) throws SQLException {
        String sql = "INSERT INTO ProductVariants (product_id, size, color, quantity, status, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        st.setInt(1, variant.getProductId());
        st.setString(2, variant.getSize());
        st.setString(3, variant.getColor());
        st.setInt(4, variant.getQuantity());
        st.setBoolean(5, variant.isStatus());
        st.executeUpdate();
        ResultSet rs = st.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    public ProductVariant getVariantByProductCodeSizeColor(String productCode, String size, String color) throws SQLException {
        String sql = "SELECT pv.variant_id, pv.product_id, pv.size, pv.color, pv.quantity, pv.status, " +
                    "p.code as product_code, p.name as product_name, c.name as category_name, c.category_id " +
                    "FROM ProductVariants pv " +
                    "INNER JOIN Products p ON pv.product_id = p.product_id " +
                    "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                    "WHERE p.code = ? AND pv.size = ? AND pv.color = ? AND pv.status = 1";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, productCode);
        st.setString(2, size);
        st.setString(3, color);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            ProductVariant variant = new ProductVariant();
            variant.setVariantId(rs.getInt("variant_id"));
            variant.setProductId(rs.getInt("product_id"));
            variant.setSize(rs.getString("size"));
            variant.setColor(rs.getString("color"));
            variant.setQuantity(rs.getInt("quantity"));
            variant.setStatus(rs.getBoolean("status"));
            variant.setProductCode(rs.getString("product_code"));
            variant.setProductName(rs.getString("product_name"));
            variant.setCategoryName(rs.getString("category_name"));
            variant.setCategoryId(rs.getInt("category_id"));
            return variant;
        }
        
        return null;
    }

    public void increaseVariantStock(int variantId, int quantity) throws SQLException {
        String sql = "UPDATE ProductVariants SET quantity = quantity + ?, updated_at = GETDATE() WHERE variant_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, quantity);
        st.setInt(2, variantId);
        st.executeUpdate();
    }

    public List<ProductVariant> getVariantsByProductCode(String productCode) throws SQLException {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT pv.variant_id, pv.product_id, pv.size, pv.color, pv.quantity, pv.status, " +
                    "p.code as product_code, p.name as product_name, c.name as category_name, c.category_id " +
                    "FROM ProductVariants pv " +
                    "INNER JOIN Products p ON pv.product_id = p.product_id " +
                    "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                    "WHERE p.code = ? AND pv.status = 1 " +
                    "ORDER BY pv.size, pv.color";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, productCode);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            ProductVariant variant = new ProductVariant();
            variant.setVariantId(rs.getInt("variant_id"));
            variant.setProductId(rs.getInt("product_id"));
            variant.setSize(rs.getString("size"));
            variant.setColor(rs.getString("color"));
            variant.setQuantity(rs.getInt("quantity"));
            variant.setStatus(rs.getBoolean("status"));
            variant.setProductCode(rs.getString("product_code"));
            variant.setProductName(rs.getString("product_name"));
            variant.setCategoryName(rs.getString("category_name"));
            variant.setCategoryId(rs.getInt("category_id"));
            variants.add(variant);
        }
        
        return variants;
    }
}
