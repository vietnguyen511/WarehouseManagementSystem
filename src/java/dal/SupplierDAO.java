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
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setName(rs.getString("name"));
                s.setPhone(rs.getString("phone"));
                s.setEmail(rs.getString("email"));
                s.setAddress(rs.getString("address"));
                s.setStatus(rs.getBoolean("status"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Error getAllSuppliers: " + e.getMessage());
        }
        return list;
    }
}


