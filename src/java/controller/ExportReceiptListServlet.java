//
//package controller;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.*;
//import java.sql.*;
//
//public class ExportReceiptListServlet extends HttpServlet {
//
//    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=WarehouseDB;encrypt=false";
//    private static final String USER = "sa";
//    private static final String PASS = "your_password";
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String action = request.getParameter("action");
//        String exportId = request.getParameter("export_id");
//
//        if ("detail".equals(action)) {
//            request.setAttribute("export_id", exportId);
//            RequestDispatcher rd = request.getRequestDispatcher("ExportReceiptDetail.jsp");
//            rd.forward(request, response);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String action = request.getParameter("action");
//        String exportId = request.getParameter("export_id");
//
//        if ("cancel".equals(action)) {
//            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
//                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
//                // Xóa chi tiết trước
//                PreparedStatement ps1 = conn.prepareStatement("DELETE FROM ExportDetails WHERE export_id=?");
//                ps1.setString(1, exportId);
//                ps1.executeUpdate();
//
//                // Xóa receipt
//                PreparedStatement ps2 = conn.prepareStatement("DELETE FROM ExportReceipts WHERE export_id=?");
//                ps2.setString(1, exportId);
//                ps2.executeUpdate();
//
//                ps1.close();
//                ps2.close();
//
//                response.sendRedirect("ExportReceiptList.jsp");
//
//            } catch (Exception e) {
//                response.setContentType("text/html;charset=UTF-8");
//                PrintWriter out = response.getWriter();
//                out.println("<h3 style='color:red'>Error: " + e.getMessage() + "</h3>");
//            }
//        }
//    }
//}


// Chỉnh sửa lúc 1:17 -- 17/10/2025
package controller;

import dal.ExportReceiptDAO;
import model.ExportReceipt;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;

@WebServlet("/list") // URL: /ExportManagement/list
public class ExportReceiptListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            // Gọi DAO để lấy dữ liệu từ DB
            ExportReceiptDAO dao = new ExportReceiptDAO();
            List<ExportReceipt> receipts = dao.getAllExportReceipt();

            // Truyền danh sách sang JSP
            request.setAttribute("data", receipts);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading receipts: " + e.getMessage());
        }

        // Forward sang JSP hiển thị
        RequestDispatcher dispatcher = request.getRequestDispatcher("ExportReceiptList.jsp");
        dispatcher.forward(request, response);
    }
}
