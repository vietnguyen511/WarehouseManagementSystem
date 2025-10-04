package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}


