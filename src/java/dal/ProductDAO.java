package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        if (rs.next()) {
            return rs.getInt(1);
        }
        return null;
    }

    public int createProduct(String code, String name, String material, String unit, BigDecimal importPrice, Integer categoryId) throws SQLException {
        String sql = "INSERT INTO Products (code, name, category_id, material, unit, quantity, import_price, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, 0, ?, 1, GETDATE(), GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        st.setString(1, code);
        st.setString(2, name != null ? name : code);
        if (categoryId != null) {
            st.setInt(3, categoryId);
        } else {
            st.setNull(3, java.sql.Types.INTEGER);
        }
        st.setString(4, material != null ? material : "");
        st.setString(5, unit != null ? unit : "piece");
        st.setBigDecimal(6, importPrice);
        st.executeUpdate();
        ResultSet rs = st.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        throw new SQLException("Failed to create product for code: " + code);
    }

    // Overloaded method for backward compatibility
    public int createProduct(String code, String name, BigDecimal importPrice) throws SQLException {
        return createProduct(code, name, "", "piece", importPrice, null);
    }

    // Overloaded method for backward compatibility
    public int createProduct(String code, String name, BigDecimal importPrice, Integer categoryId) throws SQLException {
        return createProduct(code, name, "", "piece", importPrice, categoryId);
    }

    public void increaseProductStock(int productId, int quantity, BigDecimal newImportPrice) throws SQLException {
        String sql = "UPDATE Products SET quantity = quantity + ?, import_price = ISNULL(?, import_price), updated_at = GETDATE() WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, quantity);
        st.setBigDecimal(2, newImportPrice);
        st.setInt(3, productId);
        st.executeUpdate();
    }

    public void updateImportPrice(int productId, BigDecimal newImportPrice) throws SQLException {
        String sql = "UPDATE Products SET import_price = ?, updated_at = GETDATE() WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setBigDecimal(1, newImportPrice);
        st.setInt(2, productId);
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
        String sql = "SELECT TOP 1 p.product_id, p.code, p.name, p.material, p.unit, p.quantity, p.import_price, p.export_price, p.status, p.category_id, c.name AS category_name "
                + "FROM Products p "
                + "LEFT JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE p.code = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, code);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            model.Product p = new model.Product();
            p.setProductId(rs.getInt("product_id"));
            p.setCode(rs.getString("code"));
            p.setName(rs.getString("name"));
            p.setMaterial(rs.getString("material"));
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

    /**
     * Get all active products from the database
     *
     * @return List of Product objects
     */
    public List<model.Product> getAllProducts() {
        List<model.Product> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.code, p.name, p.material, p.unit, p.quantity, p.import_price, p.export_price, p.status, p.category_id, c.name AS category_name "
                + "FROM Products p "
                + "LEFT JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE p.status = 1 "
                + "ORDER BY p.name ASC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                model.Product product = new model.Product();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setMaterial(rs.getString("material"));
                product.setUnit(rs.getString("unit"));
                product.setQuantity(rs.getInt("quantity"));
                product.setImportPrice(rs.getBigDecimal("import_price"));
                product.setExportPrice(rs.getBigDecimal("export_price"));
                product.setStatus(rs.getBoolean("status"));

                // Get category info
                int categoryId = rs.getInt("category_id");
                product.setCategoryId(rs.wasNull() ? null : categoryId);
                product.setCategoryName(rs.getString("category_name"));

                list.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error in getAllProducts: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return list;
    }

    public model.Product getProductById(int productId) {
        String sql = "SELECT p.product_id, p.code, p.name, p.material, p.unit, p.quantity, p.import_price, p.export_price, "
                + "p.status, p.category_id, c.name AS category_name, p.created_at, p.updated_at "
                + "FROM Products p "
                + "LEFT JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE p.product_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, productId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    model.Product product = new model.Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setCode(rs.getString("code"));
                    product.setName(rs.getString("name"));
                    product.setMaterial(rs.getString("material"));
                    product.setUnit(rs.getString("unit"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setImportPrice(rs.getBigDecimal("import_price"));
                    product.setExportPrice(rs.getBigDecimal("export_price"));
                    product.setStatus(rs.getBoolean("status"));
                    int categoryId = rs.getInt("category_id");
                    product.setCategoryId(rs.wasNull() ? null : categoryId);
                    product.setCategoryName(rs.getString("category_name"));
                    return product;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getProductById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<model.Product> getProductByName(String name) {
        List<model.Product> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.code, p.name, p.unit, p.quantity, "
                + "p.import_price, p.export_price, p.status, p.category_id, c.name AS category_name "
                + "FROM Products p "
                + "LEFT JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE p.name LIKE ? "
                + "ORDER BY p.name ASC";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, "%" + name + "%"); // tìm các sản phẩm có tên chứa từ khóa
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    model.Product product = new model.Product();
                    product.setProductId(rs.getInt("product_id"));
                    product.setCode(rs.getString("code"));
                    product.setName(rs.getString("name"));
                    product.setMaterial(rs.getString("material"));
                    product.setUnit(rs.getString("unit"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setImportPrice(rs.getBigDecimal("import_price"));
                    product.setExportPrice(rs.getBigDecimal("export_price"));
                    product.setStatus(rs.getBoolean("status"));
                    int categoryId = rs.getInt("category_id");
                    product.setCategoryId(rs.wasNull() ? null : categoryId);
                    product.setCategoryName(rs.getString("category_name"));
                    list.add(product);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getProductByName: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cập nhật quantity của sản phẩm = tổng quantity tất cả biến thể theo product_id
     */
    public void updateQuantityAsSumOfVariants(int productId) throws SQLException {
        String sql = "UPDATE Products SET quantity = (SELECT ISNULL(SUM(quantity),0) FROM ProductVariants WHERE product_id = ?) WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, productId);
        st.setInt(2, productId);
        st.executeUpdate();
    }
}
