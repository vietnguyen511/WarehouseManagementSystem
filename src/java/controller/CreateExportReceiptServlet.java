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
