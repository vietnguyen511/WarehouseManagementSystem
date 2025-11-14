/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Category;

/**
 * CategoryDAO - Data Access Object for Categories table Handles all database
 * operations related to product categories
 *
 * @author lengo
 */
public class CategoryDAO extends DBContext {

    // Constructor for standalone use
    public CategoryDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public CategoryDAO(Connection connection) {
        super(connection);
    }

    /**
     * Get all active categories from the database
     *
     * @return List of Category objects
     */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, code, name, description, status, created_at, updated_at "
                + "FROM Categories "
                + "ORDER BY category_id ASC";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCode(rs.getString("code"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setStatus(rs.getBoolean("status"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                category.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(category);
            }
        } catch (SQLException e) {
            System.out.println("Error in getAllCategories: " + e.getMessage());
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

    /**
     * Get category by ID
     *
     * @param categoryId The category ID to search for
     * @return Category object or null if not found
     */
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT category_id, code, name, description, status, created_at, updated_at "
                + "FROM Categories "
                + "WHERE category_id = ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, categoryId);
            rs = st.executeQuery();
            if (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCode(rs.getString("code"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setStatus(rs.getBoolean("status"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                category.setUpdatedAt(rs.getTimestamp("updated_at"));
                return category;
            }
        } catch (SQLException e) {
            System.out.println("Error in getCategoryById: " + e.getMessage());
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
        return null;
    }

    /**
     * Get categories whose name contains the given keyword (case-insensitive)
     *
     * @param keyword the search keyword
     * @return List of matching Category objects
     */
    public List<Category> getCategoryByName(String keyword) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, code, name, description, status, created_at, updated_at "
                + "FROM Categories "
                + "WHERE name LIKE ?";

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + keyword + "%");
            rs = st.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCode(rs.getString("code"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setStatus(rs.getBoolean("status"));
                category.setCreatedAt(rs.getTimestamp("created_at"));
                category.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(category);
            }

        } catch (SQLException e) {
            System.out.println("Error in getCategoryByName: " + e.getMessage());
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

    /**
     * Insert a new category into the database
     *
     * @param category Category object to be inserted
     * @return true if inserted successfully, false otherwise
     */
    public boolean insertCategory(Category category) {
        if (category == null
                || category.getCode() == null || category.getCode().trim().isEmpty()
                || category.getName() == null || category.getName().trim().isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO Categories (code, name, description, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, GETDATE(), GETDATE())";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, category.getCode());
            st.setString(2, category.getName());
            st.setString(3, category.getDescription());
            st.setBoolean(4, category.getStatus());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error in insertCategory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int insertCategoryReturnId(Category category) {
        if (category == null
                || category.getCode() == null || category.getCode().trim().isEmpty()
                || category.getName() == null || category.getName().trim().isEmpty()) {
            return -1;
        }
        String sql = "INSERT INTO Categories (code, name, description, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, GETDATE(), GETDATE())";
        try (PreparedStatement st = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setString(1, category.getCode());
            st.setString(2, category.getName());
            st.setString(3, category.getDescription());
            st.setBoolean(4, category.getStatus());
            int result = st.executeUpdate();

            if (result > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("Error in insertCategoryReturnId: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateCategory(model.Category category) {
        String sql = "UPDATE Categories "
                + "SET code = ?, name = ?, description = ?, status = ?, updated_at = GETDATE() "
                + "WHERE category_id = ?";
        try (Connection conn = dal.DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCode().trim());
            ps.setString(2, category.getName().trim());
            ps.setString(3, category.getDescription());
            ps.setBoolean(4, category.getStatus());
            ps.setInt(5, category.getCategoryId());

            // Nếu chuyển sang inactive thì gọi hàm riêng để disable các sản phẩm
            if (!category.getStatus()) {
                deactivateProductsByCategory(category.getCategoryId());
            }

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error in updateCategory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void deactivateProductsByCategory(int categoryId) throws SQLException {
        String sql = "UPDATE Products SET status = 0, updated_at = GETDATE() WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.executeUpdate();
        }
    }

    /**
     * Test method to verify database connection and data retrieval
     */
    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.getAllCategories();

        System.out.println("Total categories found: " + categories.size());
        for (Category category : categories) {
            System.out.println(category);
        }
    }
}
