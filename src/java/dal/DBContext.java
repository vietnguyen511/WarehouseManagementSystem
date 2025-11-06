/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

/**
 *
 * @author admin
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {
    protected Connection connection;

    public DBContext() {
        try {
            // Edit URL, username, password to authenticate with your MS SQL Server
            String url = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB;encrypt=true;trustServerCertificate=true;";
            String username = "sa";
            String password = "sa";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Creates and returns a new database connection to the WarehouseDB.
     * <p>
     * This static method is used when a DAO or servlet needs to open a 
     * standalone connection without instantiating a DBContext object.
     * </p>
     *
     * @return a new {@link java.sql.Connection} object if successful; 
     *         otherwise returns {@code null} if an exception occurs.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB;encrypt=true;trustServerCertificate=true;";
            String username = "sa";
            String password = "123456";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    // Constructor that accepts an existing connection (for transaction management)
    public DBContext(Connection connection) {
        this.connection = connection;
    }
}
