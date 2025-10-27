package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExportDetail;

public class ExportDetailDAO extends DBContext {

    // Constructor for standalone use
    public ExportDetailDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public ExportDetailDAO(Connection connection) {
        super(connection);
    }

    public void insertExportDetail(ExportDetail d, int exportId) throws SQLException {
        String sql = "INSERT INTO ExportDetails (export_id, variant_id, quantity, price, amount) "
                   + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, exportId);
        st.setInt(2, d.getVariantId());
        st.setInt(3, d.getQuantity());
        st.setBigDecimal(4, d.getPrice());
        st.setBigDecimal(5, d.getAmount());
        st.executeUpdate();
    }
    
    public List<ExportDetail> getExportDetailsByExportId(int exportId) throws SQLException {
        List<ExportDetail> details = new ArrayList<>();
        String sql = "SELECT ed.export_detail_id, ed.export_id, ed.variant_id, ed.quantity, " +
                    "ed.price, ed.amount, p.code as product_code, p.name as product_name, " +
                    "p.unit, c.name as category_name, pv.size, pv.color " +
                    "FROM ExportDetails ed " +
                    "INNER JOIN ProductVariants pv ON ed.variant_id = pv.variant_id " +
                    "INNER JOIN Products p ON pv.product_id = p.product_id " +
                    "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                    "WHERE ed.export_id = ? " +
                    "ORDER BY ed.export_detail_id";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, exportId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            ExportDetail detail = new ExportDetail();
            detail.setExportDetailId(rs.getInt("export_detail_id"));
            detail.setExportId(rs.getInt("export_id"));
            detail.setVariantId(rs.getInt("variant_id"));
            detail.setQuantity(rs.getInt("quantity"));
            detail.setPrice(rs.getBigDecimal("price"));
            detail.setAmount(rs.getBigDecimal("amount"));
            detail.setProductCode(rs.getString("product_code"));
            detail.setProductName(rs.getString("product_name"));
//            detail.setUnit(rs.getString("unit"));
//            detail.setCategoryName(rs.getString("category_name"));
            detail.setSize(rs.getString("size"));
            detail.setColor(rs.getString("color"));
            details.add(detail);
        }
        
        return details;
    }
}

