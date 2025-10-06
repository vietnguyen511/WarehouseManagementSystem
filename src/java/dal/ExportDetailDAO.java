package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        String sql = "INSERT INTO ExportDetails (export_id, product_id, quantity, price, amount) "
                   + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, exportId);
        st.setInt(2, d.getProductId());
        st.setInt(3, d.getQuantity());
        st.setBigDecimal(4, d.getPrice());
        st.setBigDecimal(5, d.getAmount());
        st.executeUpdate();
    }
}

