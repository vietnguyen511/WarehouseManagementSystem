package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ImportDetail;

public class ImportDetailDAO extends DBContext {

    // Constructor for standalone use
    public ImportDetailDAO() {
        super();
    }

    // Constructor for transaction management (shares connection)
    public ImportDetailDAO(Connection connection) {
        super(connection);
    }

    public void insertImportDetail(ImportDetail d, int importId) throws SQLException {
        String sql = "INSERT INTO ImportDetails (import_id, product_id, quantity, price, amount) "
                   + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, importId);
        st.setInt(2, d.getProductId());
        st.setInt(3, d.getQuantity());
        st.setBigDecimal(4, d.getPrice());
        st.setBigDecimal(5, d.getAmount());
        st.executeUpdate();
    }

    public List<ImportDetail> getImportDetailsByImportId(int importId) throws SQLException {
        List<ImportDetail> details = new ArrayList<>();
        String sql = "SELECT id.import_detail_id, id.import_id, id.product_id, id.quantity, " +
                    "id.price, id.amount, p.code as product_code, p.name as product_name, " +
                    "p.unit, c.name as category_name " +
                    "FROM ImportDetails id " +
                    "INNER JOIN Products p ON id.product_id = p.product_id " +
                    "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                    "WHERE id.import_id = ? " +
                    "ORDER BY id.import_detail_id";
        
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, importId);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            ImportDetail detail = new ImportDetail();
            detail.setImportDetailId(rs.getInt("import_detail_id"));
            detail.setImportId(rs.getInt("import_id"));
            detail.setProductId(rs.getInt("product_id"));
            detail.setQuantity(rs.getInt("quantity"));
            detail.setPrice(rs.getBigDecimal("price"));
            detail.setAmount(rs.getBigDecimal("amount"));
            detail.setProductCode(rs.getString("product_code"));
            detail.setProductName(rs.getString("product_name"));
            detail.setUnit(rs.getString("unit"));
            detail.setCategoryName(rs.getString("category_name"));
            details.add(detail);
        }
        
        return details;
    }
}


