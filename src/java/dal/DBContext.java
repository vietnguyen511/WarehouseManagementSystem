package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    protected Connection connection;

    public DBContext() {
        reconnect();
    }

    private void reconnect() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB";
            String username = "sa";
            String password = "123";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.err.println("Database connection error: " + ex.getMessage());
            throw new RuntimeException("Unable to connect to database", ex);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();
            }
        } catch (SQLException ex) {
            System.err.println("Failed to reconnect: " + ex.getMessage());
            throw new RuntimeException("Database connection is not available", ex);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.err.println("Error closing connection: " + ex.getMessage());
        }
    }
}
