//package dal;
//
//import model.Supplier;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SupplierDAO {
//    private final DBContext dbContext = new DBContext(); // Khai báo instance của DBContext
//
//    public void createSupplier(Supplier supplier) throws SQLException {
//        String sql = "INSERT INTO Suppliers (name, phone, email, address, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE())";
//        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, supplier.getName());
//            ps.setString(2, supplier.getPhone());
//            ps.setString(3, supplier.getEmail());
//            ps.setString(4, supplier.getAddress());
//            ps.setBoolean(5, supplier.isStatus());
//            ps.executeUpdate();
//        }
//    }
//
//    public List<Supplier> getAllSuppliers() throws SQLException {
//        List<Supplier> suppliers = new ArrayList<>();
//        String sql = "SELECT * FROM Suppliers";
//        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                Supplier supplier = new Supplier(
//                        rs.getInt("supplier_id"),
//                        rs.getString("name"),
//                        rs.getString("phone"),
//                        rs.getString("email"),
//                        rs.getString("address"),
//                        rs.getBoolean("status"),
//                        rs.getDate("created_at"),
//                        rs.getDate("updated_at")
//                );
//                suppliers.add(supplier);
//            }
//        }
//        return suppliers;
//    }
//}