package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;
import model.Product;

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
                + "ORDER BY p.product_id ASC";
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
        String sql = "SELECT p.product_id, p.code, p.name, p.material, p.unit, p.quantity, "
                + "       p.import_price, p.export_price, p.status, p.category_id, "
                + "       c.name AS category_name, p.description, "
                + "       p.created_at, p.updated_at "
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
                    product.setDescription(rs.getString("description"));
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
        String sql = "SELECT p.product_id, p.code, p.name, p.material, p.unit, p.quantity, "
                + "p.import_price, p.export_price, p.status, p.category_id, "
                + "c.name AS category_name "
                + "FROM Products p "
                + "LEFT JOIN Categories c ON p.category_id = c.category_id "
                + "WHERE p.name LIKE ? OR p.code LIKE ? "
                + "ORDER BY p.name ASC";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String keyword = "%" + name + "%";
            st.setString(1, keyword);
            st.setString(2, keyword);

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

    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO Products "
                + " (code, name, category_id, material, unit, quantity, "
                + "  description, status, "
                + "  created_at, updated_at) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        try (Connection conn = dal.DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());
            ps.setInt(3, product.getCategoryId());
            if (product.getMaterial() == null || product.getMaterial().trim().isEmpty()) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setString(4, product.getMaterial().trim());
            }
            if (product.getUnit() == null || product.getUnit().trim().isEmpty()) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setString(5, product.getUnit().trim());
            }
            ps.setInt(6, product.getQuantity());
            if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
                ps.setNull(7, Types.NVARCHAR);
            } else {
                ps.setString(7, product.getDescription().trim());
            }
            ps.setBoolean(8, product.isStatus());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error in insertProduct: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET "
                + "code = ?, name = ?, category_id = ?, material = ?, unit = ?, "
                + "description = ?, status = ?, "
                + "updated_at = GETDATE() "
                + "WHERE product_id = ?";

        try (Connection conn = dal.DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());

            if (product.getCategoryId() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, product.getCategoryId());
            }

            if (product.getMaterial() == null || product.getMaterial().trim().isEmpty()) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setString(4, product.getMaterial().trim());
            }

            if (product.getUnit() == null || product.getUnit().trim().isEmpty()) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setString(5, product.getUnit().trim());
            }

            if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
                ps.setNull(6, Types.NVARCHAR);
            } else {
                ps.setString(6, product.getDescription().trim());
            }
            ps.setBoolean(7, product.isStatus());
            ps.setInt(8, product.getProductId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error in updateProduct: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật quantity của sản phẩm = tổng quantity tất cả biến thể theo
     * product_id
     */
    public void updateQuantityAsSumOfVariants(int productId) throws SQLException {
        String sql = "UPDATE Products SET quantity = (SELECT ISNULL(SUM(quantity),0) FROM ProductVariants WHERE product_id = ?) WHERE product_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, productId);
        st.setInt(2, productId);
        st.executeUpdate();
    }

    public BigDecimal getLatestImportPrice(int productId) {
        String sql = "SELECT TOP 1 price FROM ImportDetails "
                + "WHERE variant_id IN (SELECT variant_id FROM ProductVariants WHERE product_id = ?) "
                + "ORDER BY import_detail_id DESC";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, productId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getLatestExportPrice(int productId) {
        String sql = "SELECT TOP 1 price FROM ExportDetails "
                + "WHERE variant_id IN (SELECT variant_id FROM ProductVariants WHERE product_id = ?) "
                + "ORDER BY export_detail_id DESC";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, productId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
