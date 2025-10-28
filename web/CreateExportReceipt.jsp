<%-- 
    Document   : CreateExportReceipt
    Created on : Oct 16, 2025, 4:58:30 PM
    Author     : MSII
--%>

<%--
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h2>Create Export Receipt</h2>

        <form action="CreateExportReceiptServlet" method="post">
            <table>
                <tr>
                    <td>Customer ID:</td>
                    <td><input type="text" name="customer_id" required></td>
                </tr>
                <tr>
                     <td>Product ID:</td>
                     <td><input type="text" name="product_id" required></td>
                </tr>
                <tr>
                     <td>Price:</td>
                     <td><input type="number" step="0.01" name="price" required></td>
                </tr>
                <tr>
                     <td>Quantity:</td>
                     <td><input type="number" name="quantity" required></td>
                </tr>
                <tr>
                     <td>Note:</td>
                     <td><textarea name="note" rows="3" cols="30"></textarea></td>
                </tr>
                <tr>
                     <td colspan="2"><input type="submit" value="Create Receipt"></td>
                </tr>
            </table>
        </form>

    </body>
</html>
--%>


<%-- Chỉnh sửa lúc 1:09 - 17/10/2025 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Export Receipt</title>
</head>
<body>
    <h2>Create Export Receipt</h2>
    <form action="create" method="post">
        <label>Product ID:</label><br>
        <input type="text" name="product_id" required><br><br>

        <label>Customer ID:</label><br>
        <input type="number" name="customer_id" required><br><br>

        <label>User ID:</label><br>
        <input type="number" name="user_id" required><br><br>

        <label>Price:</label><br>
        <input type="number" step="0.01" name="price" required><br><br>

        <label>Quantity:</label><br>
        <input type="number" name="quantity" required><br><br>

        <label>Note:</label><br>
        <textarea name="note"></textarea><br><br>

        <label>Date:</label><br>
        <input type="date" name="date" required><br><br>

        <input type="submit" value="Create">
    </form>

    <% 
        String message = (String) request.getAttribute("message");
        if (message != null) { 
    %>
        <p style="color: green;"><%= message %></p>
    <% } %>
</body>
</html>
