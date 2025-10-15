package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Supplier;

public class SupplierDAO extends DBContext {

    // Constructor for standalone use
    public SupplierDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public SupplierDAO(Connection connection) {
        super(connection);
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT supplier_id, name, phone, email, address, status FROM Suppliers WHERE status = 1 ORDER BY name";
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
    public Supplier findById(int id) throws SQLException {
        String sql = "SELECT supplier_id, name, phone, email, address, status FROM Suppliers WHERE supplier_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) return mapRow(rs);
        return null;
    }

    public int create(Supplier s) throws SQLException {
        String sql = "INSERT INTO Suppliers(name, phone, email, address, status, created_at, updated_at) VALUES(?,?,?,?,?,GETDATE(),GETDATE())";
        PreparedStatement st = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
        st.setString(1, s.getName());
        st.setString(2, s.getPhone());
        st.setString(3, s.getEmail());
        st.setString(4, s.getAddress());
        st.setBoolean(5, s.isStatus());
        int affected = st.executeUpdate();
        if (affected == 0) return 0;
        ResultSet keys = st.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        return 0;
    }

    public boolean update(Supplier s) throws SQLException {
        String sql = "UPDATE Suppliers SET name = ?, phone = ?, email = ?, address = ?, status = ?, updated_at = GETDATE() WHERE supplier_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, s.getName());
        st.setString(2, s.getPhone());
        st.setString(3, s.getEmail());
        st.setString(4, s.getAddress());
        st.setBoolean(5, s.isStatus());
        st.setInt(6, s.getSupplierId());
        return st.executeUpdate() > 0;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Suppliers WHERE supplier_id = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, id);
        return st.executeUpdate() > 0;
    }

    // Search, filter, pagination
    public int countAll(String searchTerm, String statusFilter) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM Suppliers WHERE 1=1");
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

    public List<Supplier> findAll(int offset, int limit, String sortBy, String sortDir, String searchTerm, String statusFilter) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT supplier_id, name, phone, email, address, status FROM Suppliers WHERE 1=1 ");
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
        List<Supplier> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private Supplier mapRow(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setSupplierId(rs.getInt("supplier_id"));
        s.setName(rs.getString("name"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setStatus(rs.getBoolean("status"));
        return s;
    }
}


