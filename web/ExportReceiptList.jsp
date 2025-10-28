<%-- 
    Document   : ExportReceiptList
    Created on : Oct 16, 2025, 5:51:25 PM
    Author     : MSII
--%>

<%--
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Export Receipt List</title>
</head>
<body>
<h2>Export Receipt List</h2>

<%
    String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB;encrypt=false";
    String USER = "sa";
    String PASS = "your_password";

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        stmt = conn.createStatement();

        String sql = "SELECT export_id, customer_id, user_id, date, total_quantity, total_amount, note FROM ExportReceipts ORDER BY created_at DESC";
        rs = stmt.executeQuery(sql);
%>

<table border="1" cellpadding="5" cellspacing="0">
    <tr>
        <th>Export ID</th>
        <th>Customer ID</th>
        <th>User ID</th>
        <th>Date</th>
        <th>Total Quantity</th>
        <th>Total Amount</th>
        <th>Note</th>
        <th>Actions</th>
    </tr>

<%
        while (rs.next()) {
            String exportId = rs.getString("export_id");
%>
    <tr>
        <td><%= exportId %></td>
        <td><%= rs.getString("customer_id") %></td>
        <td><%= rs.getString("user_id") %></td>
        <td><%= rs.getString("date") %></td>
        <td><%= rs.getInt("total_quantity") %></td>
        <td><%= rs.getDouble("total_amount") %></td>
        <td><%= rs.getString("note") %></td>
        <td>
            <form action="ExportReceiptServlet" method="get" style="display:inline;">
                <input type="hidden" name="action" value="detail">
                <input type="hidden" name="export_id" value="<%= exportId %>">
                <input type="submit" value="Detail">
            </form>

            <form action="ExportReceiptServlet" method="post" style="display:inline;" 
                  onsubmit="return confirm('Do you want to Cancel Export?');">
                <input type="hidden" name="action" value="cancel">
                <input type="hidden" name="export_id" value="<%= exportId %>">
                <input type="submit" value="Cancel">
            </form>
        </td>
    </tr>
<%
        }
    } catch (Exception e) {
        out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
    } finally {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
%>
</table>

</body>
</html>
--%>


<%-- Chỉnh sửa lúc 1:11 - 17/10/2025 --%>
<%@ page import="java.util.*, model.ExportReceipt" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Export Receipt List</title>
</head>
<body>
    <h2>Export Receipt List</h2>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Export ID</th>
            <th>Customer ID</th>
            <th>User ID</th>
            <th>Date</th>
            <th>Total Quantity</th>
            <th>Total Amount</th>
            <th>Note</th>
            <th>Action</th>
        </tr>

        <%
            List<ExportReceipt> list = (List<ExportReceipt>) request.getAttribute("data");
            if (list != null && !list.isEmpty()) {
                for (ExportReceipt r : list) {
        %>
        <tr>
            <td><%= r.getExportId() %></td>
            <td><%= r.getCustomerId() %></td>
            <td><%= r.getUserId() %></td>
            <td><%= r.getDate() %></td>
            <td><%= r.getTotalQuantity() %></td>
            <td><%= r.getTotalAmount() %></td>
            <td><%= r.getNote() %></td>
            <td>
                <a href="detail?export_id=<%= r.getExportId() %>">Detail</a> 
                <a href="cancel?export_id=<%= r.getExportId() %>">Cancel</a>
            </td>
        </tr>
        <%      }
            } else { %>
        <tr><td colspan="8">No data available</td></tr>
        <% } %>
    </table>

    <br>
    
</body>
</html>
