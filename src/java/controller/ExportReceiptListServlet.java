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
