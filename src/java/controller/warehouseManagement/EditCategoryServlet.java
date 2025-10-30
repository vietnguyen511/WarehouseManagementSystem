/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.warehouseManagement;

import dal.ActivityLogHelper;
import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.Category;

/**
 *
 * @author DANG
 */
public class EditCategoryServlet extends HttpServlet {

    private CategoryDAO dao = new CategoryDAO();

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
            response.sendRedirect(request.getContextPath() + "/warehouse-management/category-management?msg=invalid");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw.trim());
            Category category = dao.getCategoryById(id);
            if (category == null) {
                response.sendRedirect(request.getContextPath() + "/warehouse-management/category-management?msg=notfound");
                return;
            }

            request.setAttribute("category", category);
            request.getRequestDispatcher("/warehouse-management/edit-category.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/warehouse-management/category-management?msg=error");
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

        request.setCharacterEncoding("UTF-8");
        String idRaw = request.getParameter("id");
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String statusStr = request.getParameter("status");

        String redirectUrl = request.getContextPath() + "/warehouse-management/category-management";

        try {
            int id = Integer.parseInt(idRaw.trim());
            boolean status = "1".equals(statusStr) || "active".equalsIgnoreCase(statusStr);

            Category category = new Category();
            category.setCategoryId(id);
            category.setCode(code);
            category.setName(name);
            category.setDescription(description);
            category.setStatus(status);

            boolean success = dao.updateCategory(category);

            if (success) {
                ActivityLogHelper.logUpdate(request.getSession(), "Categories", id,
                        "Updated category: " + name + " (" + code + ")");
                response.sendRedirect(redirectUrl + "?msg=updated");
            } else {
                response.sendRedirect(redirectUrl + "?msg=updateFail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(redirectUrl + "?msg=error");
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
