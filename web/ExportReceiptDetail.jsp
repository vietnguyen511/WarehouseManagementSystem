<%-- 
    Document   : ExportReceiptDetail
    Created on : Oct 16, 2025, 7:52:40 PM
    Author     : MSII
--%>

<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Export Receipt Detail</title>
</head>
<body>
<h2>Export Receipt Detail</h2>

<%
    String exportId = (String) request.getAttribute("export_id");
    if (exportId == null) exportId = request.getParameter("export_id");

    String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB;encrypt=false";
    String USER = "sa";
    String PASS = "your_password";

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        // Thông tin hóa đơn
        String sql1 = "SELECT * FROM ExportReceipts WHERE export_id=?";
        ps = conn.prepareStatement(sql1);
        ps.setString(1, exportId);
        rs = ps.executeQuery();

        if (rs.next()) {
%>
        <p><b>Export ID:</b> <%= rs.getString("export_id") %></p>
        <p><b>Customer ID:</b> <%= rs.getString("customer_id") %></p>
        <p><b>User ID:</b> <%= rs.getString("user_id") %></p>
        <p><b>Date:</b> <%= rs.getString("date") %></p>
        <p><b>Total Quantity:</b> <%= rs.getInt("total_quantity") %></p>
        <p><b>Total Amount:</b> <%= rs.getDouble("total_amount") %></p>
        <p><b>Note:</b> <%= rs.getString("note") %></p>
        <hr>
        <h3>Export Details:</h3>
        <table border="1" cellpadding="5">
            <tr>
                <th>Product ID</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Amount</th>
            </tr>
<%
            rs.close();
            ps.close();

            String sql2 = "SELECT * FROM ExportDetails WHERE export_id=?";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, exportId);
            rs = ps.executeQuery();

            while (rs.next()) {
%>
            <tr>
                <td><%= rs.getString("product_id") %></td>
                <td><%= rs.getInt("quantity") %></td>
                <td><%= rs.getDouble("price") %></td>
                <td><%= rs.getDouble("amount") %></td>
            </tr>
<%
            }
        } else {
            out.println("<p style='color:red'>Receipt not found.</p>");
        }
    } catch (Exception e) {
        out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
    } finally {
        if (rs != null) rs.close();
        if (ps != null) ps.close();
        if (conn != null) conn.close();
    }
%>
        </table>
        <br>
        <a href="ExportReceiptList.jsp">Back to list</a>

</body>
</html>
