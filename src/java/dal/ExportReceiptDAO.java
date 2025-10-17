
package dal;

import model.ExportReceipt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportReceiptDAO {
    private final DBContext dbContext = new DBContext();    
    //Đã sửa.
public int createExportDetails(ExportReceipt receipt) throws SQLException {
    // SAI: VALUE → ĐÚNG: VALUES, GENERATE() → GETDATE()
    String sql = "INSERT INTO ExportReceipts(customer_id, user_id, date, total_quantity, total_amount, note, created_at) VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
    try (Connection conn = dbContext.connection;
         PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, receipt.getCustomerId());
        ps.setInt(2, receipt.getUserId());
        ps.setDate(3, receipt.getDate() != null ? new java.sql.Date(receipt.getDate().getTime()) : null);
        ps.setInt(4, receipt.getTotalQuantity());
        ps.setDouble(5, receipt.getTotalAmount());
        ps.setString(6, receipt.getNote());
        ps.executeUpdate();

        int generatedId = 0;
        try (ResultSet generatedKey = ps.getGeneratedKeys()) {
            if (generatedKey.next()) {
                generatedId = generatedKey.getInt(1);
            }
        }
        return generatedId;
    }
}

public List<ExportReceipt> getAllExportReceipt() throws SQLException {
    List<ExportReceipt> receipts = new ArrayList<>();
    String sql = "SELECT * FROM ExportReceipts"; // Fix sai chính tả
    try (Connection conn = dbContext.connection;
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            ExportReceipt receipt = new ExportReceipt(
                    rs.getInt("export_id"),
                    rs.getInt("customer_id"),
                    rs.getInt("user_id"),
                    rs.getDate("date"),
                    rs.getInt("total_quantity"),
                    rs.getDouble("total_amount"),
                    rs.getString("note"),
                    rs.getString("created_at")
            );
            receipts.add(receipt);
        }
    }
    return receipts;
}
}