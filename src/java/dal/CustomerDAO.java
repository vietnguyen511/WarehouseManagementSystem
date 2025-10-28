package dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Customer;

public class CustomerDAO extends DBContext {

    // Constructor for standalone use
    public CustomerDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public CustomerDAO(Connection connection) {
        super(connection);
    }

    public List<Customer> getAllSuppliers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, name, phone, email, address, status FROM Customers WHERE status = 1 ORDER BY name";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getAllSuppliers: " + e.getMessage());
        }
        return list;
    }

    // CRUD
    public Customer findById(int id) throws SQLException {
        String sql = "SELECT customer_id, name, phone, email, address, status FROM Customers WHERE customer_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public int create(Customer c) throws SQLException {
        String sql = "INSERT INTO Customers(name, phone, email, address, status, created_at, updated_at) VALUES(?,?,?,?,?,GETDATE(),GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
        st.setString(1, c.getName());
        st.setString(2, c.getPhone());
        st.setString(3, c.getEmail());
        st.setString(4, c.getAddress());
        st.setBoolean(5, c.isStatus());
        int affected = st.executeUpdate();
        if (affected == 0) return 0;
        ResultSet keys = st.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        return 0;
    }

    public boolean update(Customer c) throws SQLException {
        String sql = "UPDATE Customers SET name = ?, phone = ?, email = ?, address = ?, status = ?, updated_at = GETDATE() WHERE customer_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, c.getName());
        st.setString(2, c.getPhone());
        st.setString(3, c.getEmail());
        st.setString(4, c.getAddress());
        st.setBoolean(5, c.isStatus());
        st.setInt(6, c.getCustomerId());
        return st.executeUpdate() > 0;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        return st.executeUpdate() > 0;
    }

    // Search, filter, pagination
    public int countAll(String searchTerm, String statusFilter) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM Customers WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sb.append(" AND (name LIKE ? OR phone LIKE ? OR email LIKE ? OR address LIKE ?) ");
            String like = "%" + searchTerm.trim() + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sb.append(" AND status = ? ");
            params.add("active".equalsIgnoreCase(statusFilter) ? 1 : 0);
        }
        PreparedStatement st = connection.prepareStatement(sb.toString());
        for (int i = 0; i < params.size(); i++) st.setObject(i + 1, params.get(i));
        ResultSet rs = st.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }

    public List<Customer> findAll(int offset, int limit, String sortBy, String sortDir, String searchTerm, String statusFilter) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT customer_id, name, phone, email, address, status FROM Customers WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sb.append(" AND (name LIKE ? OR phone LIKE ? OR email LIKE ? OR address LIKE ?) ");
            String like = "%" + searchTerm.trim() + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sb.append(" AND status = ? ");
            params.add("active".equalsIgnoreCase(statusFilter) ? 1 : 0);
        }
        // Sorting
        String orderBy = "name";
        if ("email".equalsIgnoreCase(sortBy) || "phone".equalsIgnoreCase(sortBy) || "address".equalsIgnoreCase(sortBy) || "status".equalsIgnoreCase(sortBy)) {
            orderBy = sortBy;
        }
        String direction = "ASC";
        if ("desc".equalsIgnoreCase(sortDir)) direction = "DESC";
        sb.append(" ORDER BY ").append(orderBy).append(" ").append(direction);
        sb.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        PreparedStatement st = connection.prepareStatement(sb.toString());
        for (int i = 0; i < params.size(); i++) st.setObject(i + 1, params.get(i));
        ResultSet rs = st.executeQuery();
        List<Customer> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setStatus(rs.getBoolean("status"));
        return c;
    }

    /**
     * Get import statistics for a supplier
     * Returns an array: [totalReceipts, totalQuantity, totalAmount]
     */
    public Object[] getExportStatistics(int customerId) throws SQLException {
        String sql = "SELECT " +
                    "ISNULL(COUNT(*), 0) AS total_receipts, " +
                    "ISNULL(SUM(total_quantity), 0) AS total_quantity, " +
                    "ISNULL(SUM(total_amount), 0) AS total_amount " +
                    "FROM ExportReceipts " +
                    "WHERE customer_id = ?";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, customerId);
        ResultSet rs = st.executeQuery();
        
        if (rs.next()) {
            int totalReceipts = rs.getInt("total_receipts");
            int totalQuantity = rs.getInt("total_quantity");
            BigDecimal totalAmount = rs.getBigDecimal("total_amount");
            
            return new Object[]{totalReceipts, totalQuantity, totalAmount};
        }
        
        return new Object[]{0, 0, BigDecimal.ZERO};
    }
}


