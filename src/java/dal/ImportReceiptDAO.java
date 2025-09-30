//package dal;
//
//import model.ImportReceipt;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ImportReceiptDAO {
//    private final DBContext dbContext = new DBContext();
//
//    public int createImportReceipt(ImportReceipt receipt) throws SQLException {
//        String sql = "INSERT INTO ImportReceipts (supplier_id, user_id, date, total_quantity, total_amount, note, created_at) VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
//        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
//            ps.setInt(1, receipt.getSupplierId());
//            ps.setInt(2, receipt.getUserId());
//            ps.setDate(3, receipt.getDate() != null ? new java.sql.Date(receipt.getDate().getTime()) : null);
//            ps.setInt(4, receipt.getTotalQuantity());
//            ps.setDouble(5, receipt.getTotalAmount());
//            ps.setString(6, receipt.getNote());
//            ps.executeUpdate();
//
//            int generatedId = 0;
//            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    generatedId = generatedKeys.getInt(1);
//                }
//            }
//            return generatedId;
//        }
//    }
//
//    public List<ImportReceipt> getAllImportReceipts() throws SQLException {
//        List<ImportReceipt> receipts = new ArrayList<>();
//        String sql = "SELECT * FROM ImportReceipts";
//        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                ImportReceipt receipt = new ImportReceipt(
//                    rs.getInt("import_id"),
//                    rs.getInt("supplier_id"),
//                    rs.getInt("user_id"),
//                    rs.getDate("date"),
//                    rs.getInt("total_quantity"),
//                    rs.getDouble("total_amount"),
//                    rs.getString("note"),
//                    rs.getDate("created_at")
//                );
//                receipts.add(receipt);
//            }
//        }
//        return receipts;
//    }
//}