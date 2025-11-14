/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.productsAndCategories;

import dal.ProductDAO;
import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Product;
import model.Category;

/**
 *
 * @author DANG
 */
public class ViewProductDetailsServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

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
        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath()
                    + "/warehouse-management/product-management?msg=invalid");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw.trim());
            Product product = productDAO.getProductById(id);
            List<Category> categories = categoryDAO.getAllCategories();

            if (product == null) {
                response.sendRedirect(request.getContextPath()
                        + "/warehouse-management/product-management?msg=notfound");
                return;
            }

            // >>> THÊM KHỐI NÀY: build previewUrl an toàn
            String previewUrl = null;
            if (product.getImage() != null && !product.getImage().isBlank()) {
                String img = product.getImage().trim().replace("\\", "/"); // đổi \ thành /
                if (img.startsWith("http://") || img.startsWith("https://")) {
                    // URL tuyệt đối
                    previewUrl = img;
                } else if (img.startsWith("/")) {
                    // đường dẫn tuyệt đối trong app
                    previewUrl = request.getContextPath() + img;
                } else {
                    // đường dẫn tương đối lưu trong DB (ví dụ uploads/products/abc.jpg)
                    previewUrl = request.getContextPath() + "/" + img;
                }
            }
            request.setAttribute("previewUrl", previewUrl);

            request.setAttribute("product", product);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/warehouse-management/view-product-details.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath()
                    + "/warehouse-management/product-management?msg=error");
        }
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

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "View product details (read-only)";
    }// </editor-fold>

}
