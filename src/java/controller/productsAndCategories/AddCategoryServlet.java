/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.productsAndCategories;

import dal.ActivityLogHelper;
import dal.CategoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;

/**
 *
 * @author DANG
 */
public class AddCategoryServlet extends HttpServlet {
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
        request.getRequestDispatcher("/warehouse-management/add-category.jsp").forward(request, response);
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

        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String statusStr = request.getParameter("status");
        
        boolean status = "active".equalsIgnoreCase(statusStr);

        if (code == null || code.trim().isEmpty()
                || name == null || name.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/warehouse-management/add-category?msg=addFail");
            request.getRequestDispatcher("/warehouse-management/add-category.jsp").forward(request, response);
            return;
        }

        Category category = new Category();
        category.setCode(code);
        category.setName(name);
        category.setDescription(description);
        category.setStatus(status);

        CategoryDAO dao = new CategoryDAO();
        int categoryId = dao.insertCategoryReturnId(category);

        // check if success
        if (categoryId > 0) {
            // Log activity
            ActivityLogHelper.logCreate(request.getSession(), "Categories", categoryId, 
                "Created new category: " + name + " (" + code + ")");
            
            response.sendRedirect(request.getContextPath() + "/warehouse-management/category-management?msg=added");
        } else {
            response.sendRedirect(request.getContextPath() + "/warehouse-management/add-category?msg=addFail");
            request.setAttribute("category", category);
            request.getRequestDispatcher("/warehouse-management/add-category.jsp").forward(request, response);
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
