/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.warehouseManagement;

import dal.ActivityLogHelper;
import dal.CategoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author DANG
 */
public class DeleteCategoryServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DeleteCategoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteCategoryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        request.getRequestDispatcher("/warehouse-management/delete-category.jsp").forward(request, response);
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
        String redirectUrl = request.getContextPath() + "/warehouse-management/category-management";

        if (idRaw == null || idRaw.isEmpty()) {
            response.sendRedirect(redirectUrl + "?msg=invalid");
            return;
        }

        int categoryId;
        try {
            categoryId = Integer.parseInt(idRaw.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "?msg=invalid");
            return;
        }

        CategoryDAO dao = new CategoryDAO();

        // Lấy thông tin Category trước khi xóa để ghi log
        String categoryName = "category_" + categoryId;
        try {
            model.Category category = dao.getCategoryById(categoryId);
            if (category != null) {
                categoryName = category.getName() + " (" + category.getCode() + ")";
            }
        } catch (Exception e) {
            // ignore
        }

        boolean success = dao.deleteCategoryById(categoryId);

        if (success) {
            // Ghi log khi xóa thành công
            ActivityLogHelper.logDelete(request.getSession(), "Categories", categoryId,
                    "Deleted category: " + categoryName);
            response.sendRedirect(redirectUrl + "?msg=deleted");
        } else {
            // Nếu không xóa được thì kiểm tra nguyên nhân (thường là do có Product)
            response.sendRedirect(redirectUrl + "?msg=hasProducts");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
