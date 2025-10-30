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
                + "WHERE p.status = 1 "
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
                + "       c.name AS category_name, p.description, p.image, "
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
                    product.setImage(rs.getString("image"));
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
                + "  import_price, export_price, description, image, status, "
                + "  created_at, updated_at) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

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
            if (product.getImportPrice() == null) {
                ps.setNull(7, Types.DECIMAL);
            } else {
                ps.setBigDecimal(7, product.getImportPrice());
            }
            if (product.getExportPrice() == null) {
                ps.setNull(8, Types.DECIMAL);
            } else {
                ps.setBigDecimal(8, product.getExportPrice());
            }
            if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
                ps.setNull(9, Types.NVARCHAR);
            } else {
                ps.setString(9, product.getDescription().trim());
            }

            // 10. image (nullable)
            if (product.getImage() == null || product.getImage().trim().isEmpty()) {
                ps.setNull(10, Types.NVARCHAR);
            } else {
                ps.setString(10, product.getImage().trim());
            }

            // 11. status (bit)
            ps.setBoolean(11, product.isStatus());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error in insertProduct: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProductById(int productId) {
        // SQL kiểm tra quan hệ phụ thuộc
        String checkVariants = "SELECT COUNT(*) FROM ProductVariants WHERE product_id = ?";
        String checkImports = "SELECT COUNT(*) FROM ImportDetails "
                + "WHERE variant_id IN (SELECT variant_id FROM ProductVariants WHERE product_id = ?)";
        String checkExports = "SELECT COUNT(*) FROM ExportDetails "
                + "WHERE variant_id IN (SELECT variant_id FROM ProductVariants WHERE product_id = ?)";
        String deleteSql = "DELETE FROM Products WHERE product_id = ?";

        try (Connection conn = dal.DBContext.getConnection()) {

            // Kiểm tra xem sản phẩm có variant không
            try (PreparedStatement ps = conn.prepareStatement(checkVariants)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Cannot delete: product still has variants.");
                        return false;
                    }
                }
            }

            // Kiểm tra xem variant có nằm trong phiếu nhập/xuất không
            try (PreparedStatement ps1 = conn.prepareStatement(checkImports); PreparedStatement ps2 = conn.prepareStatement(checkExports)) {

                ps1.setInt(1, productId);
                ps2.setInt(1, productId);

                try (ResultSet rs1 = ps1.executeQuery(); ResultSet rs2 = ps2.executeQuery()) {

                    boolean hasImport = rs1.next() && rs1.getInt(1) > 0;
                    boolean hasExport = rs2.next() && rs2.getInt(1) > 0;

                    if (hasImport || hasExport) {
                        System.out.println("Cannot delete: product used in import/export details.");
                        return false;
                    }
                }
            }

            // Nếu mọi thứ đều OK → xóa sản phẩm
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, productId);
                int rows = psDelete.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error in deleteProductById: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET "
                + "code = ?, name = ?, category_id = ?, material = ?, unit = ?, "
                + "import_price = ?, export_price = ?, description = ?, image = ?, status = ?, "
                + "updated_at = GETDATE() "
                + "WHERE product_id = ?";

        try (Connection conn = dal.DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());
            ps.setInt(3, product.getCategoryId());
            ps.setString(4, product.getMaterial());
            ps.setString(5, product.getUnit());
            ps.setBigDecimal(6, product.getImportPrice());
            ps.setBigDecimal(7, product.getExportPrice());
            ps.setString(8, product.getDescription());
            ps.setString(9, product.getImage());
            ps.setBoolean(10, product.isStatus());
            ps.setInt(11, product.getProductId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error in updateProduct: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
