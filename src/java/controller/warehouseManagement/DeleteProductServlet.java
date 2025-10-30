/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.warehouseManagement;

import dal.ActivityLogHelper;
import dal.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author DANG
 */
public class DeleteProductServlet extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");
        String redirectUrl = request.getContextPath() + "/warehouse-management/product-management";

        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(redirectUrl + "?msg=invalid");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(idRaw.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "?msg=invalid");
            return;
        }

        ProductDAO dao = new ProductDAO();

        // Lấy thông tin product để ghi log
        String productName = "product_" + productId;
        try {
            model.Product p = dao.getProductById(productId);
            if (p != null) {
                productName = p.getName() + " (" + p.getCode() + ")";
            }
        } catch (Exception e) {
            // ignore lỗi fetch
        }

        boolean success = dao.deleteProductById(productId);

        if (success) {
            // Ghi log khi xóa thành công
            ActivityLogHelper.logDelete(request.getSession(),
                    "Products", productId, "Deleted product: " + productName);

            response.sendRedirect(redirectUrl + "?msg=deleted");
        } else {
            // Không xóa được vì có variant / import / export liên quan
            response.sendRedirect(redirectUrl + "?msg=hasRelations");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Delete product if no variants or import/export relations exist";
    }

}
