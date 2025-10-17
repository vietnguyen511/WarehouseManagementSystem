//package controller;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.*;
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class CreateExportReceiptServlet extends HttpServlet {
//
//    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB;encrypt=false";
//    private static final String USER = "sa";
//    private static final String PASS = "your_password";
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//
//        String customerId = request.getParameter("customer_id");
//        String productId = request.getParameter("product_id");
//        double price = Double.parseDouble(request.getParameter("price"));
//        int quantity = Integer.parseInt(request.getParameter("quantity"));
//        String note = request.getParameter("note");
//
//        double totalAmount = price * quantity;
//        LocalDateTime now = LocalDateTime.now();
//        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        String createdAt = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//        Connection conn = null;
//        PreparedStatement psReceipt = null;
//        PreparedStatement psDetail = null;
//        ResultSet rs = null;
//
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            conn = DriverManager.getConnection(DB_URL, USER, PASS);
//
//            
//            String exportId = "EXP" + System.currentTimeMillis();
//
//            
//            String sqlReceipt = "INSERT INTO ExportReceipts (export_id, customer_id, user_id, date, total_quantity, total_amount, note, created_at) " +
//                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//            psReceipt = conn.prepareStatement(sqlReceipt);
//            psReceipt.setString(1, exportId);
//            psReceipt.setString(2, customerId);
//            psReceipt.setString(3, "U001"); // tạm hardcode user_id (sau này sẽ lấy từ session)
//            psReceipt.setString(4, date);
//            psReceipt.setInt(5, quantity);
//            psReceipt.setDouble(6, totalAmount);
//            psReceipt.setString(7, note);
//            psReceipt.setString(8, createdAt);
//            psReceipt.executeUpdate();
//
//            // --- 
//            String sqlDetail = "INSERT INTO ExportDetails (export_detail_id, export_id, product_id, quantity, price, amount) " +
//                               "VALUES (?, ?, ?, ?, ?, ?)";
//            psDetail = conn.prepareStatement(sqlDetail);
//            psDetail.setString(1, "ED" + System.currentTimeMillis()); // export_detail_id tự tạo
//            psDetail.setString(2, exportId);
//            psDetail.setString(3, productId);
//            psDetail.setInt(4, quantity);
//            psDetail.setDouble(5, price);
//            psDetail.setDouble(6, totalAmount);
//            psDetail.executeUpdate();
//
//            // ---
//            out.println("<html><body>");
//            out.println("Export Receipt Created Successfully!</h2>");
//            out.println("<p><b>Export ID:</b> " + exportId + "</p>");
//            out.println("<p><b>Customer ID:</b> " + customerId + "</p>");
//            out.println("<p><b>Product ID:</b> " + productId + "</p>");
//            out.println("<p><b>Quantity:</b> " + quantity + "</p>");
//            out.println("<p><b>Price:</b> " + price + "</p>");
//            out.println("<p><b>Total Amount:</b> " + totalAmount + "</p>");
//            out.println("<p><b>Note:</b> " + note + "</p>");
//            out.println("<p><b>Created At:</b> " + createdAt + "</p>");
//            out.println("</body></html>");
//
//        } catch (Exception e) {
//            out.println("<h3 style='color:red'>❌ Error: " + e.getMessage() + "</h3>");
//            e.printStackTrace(out);
//        } finally {
//            try {
//                if (rs != null) rs.close();
//                if (psReceipt != null) psReceipt.close();
//                if (psDetail != null) psDetail.close();
//                if (conn != null) conn.close();
//            } catch (SQLException ex) {
//                ex.printStackTrace(out);
//            }
//        }
//    }
//}


//Chỉnh sửa lúc 1:18 -- 17/10/2025
package controller;

import dal.ExportReceiptDAO;
import model.ExportReceipt;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/create")
public class CreateExportReceiptServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int customerId = Integer.parseInt(request.getParameter("customer_id"));
            int userId = Integer.parseInt(request.getParameter("user_id"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            double price = Double.parseDouble(request.getParameter("price"));
            String note = request.getParameter("note");
            Date date = Date.valueOf(request.getParameter("date"));
            double totalAmount = price * quantity;

            // Tạo đối tượng ExportReceipt
            ExportReceipt receipt = new ExportReceipt();
            receipt.setCustomerId(customerId);
            receipt.setUserId(userId);
            receipt.setDate(date);
            receipt.setTotalQuantity(quantity);
            receipt.setTotalAmount(totalAmount);
            receipt.setNote(note);

            // Gọi DAO lưu vào DB
            ExportReceiptDAO dao = new ExportReceiptDAO();
            int newId = dao.createExportDetails(receipt);

            if (newId > 0) {
                request.setAttribute("message", "Create successfully! Export ID: " + newId);
            } else {
                request.setAttribute("message", "Failed to create export receipt.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error: " + e.getMessage());
        }

        request.getRequestDispatcher("CreateExportReceipt.jsp").forward(request, response);
    }
}
