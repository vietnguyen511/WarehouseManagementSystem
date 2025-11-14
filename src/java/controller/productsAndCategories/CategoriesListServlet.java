/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.productsAndCategories;

import dal.CategoryDAO;
import model.Category;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DANG
 */
public class CategoriesListServlet extends HttpServlet {

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
        String searchValue = request.getParameter("searchValue");
        CategoryDAO dao = new CategoryDAO();
        List<Category> categoryList = new ArrayList<>();
        try {
            // check if search value is not null and its trimmed value is not empty
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(searchValue.trim());
                    Category category = dao.getCategoryById(categoryId);
                    // check if category is not null
                    if (category != null) {
                        categoryList.add(category);
                    }
                } catch (NumberFormatException e) {
                    categoryList = dao.getCategoryByName(searchValue.trim());
                }
            } else {
                categoryList = dao.getAllCategories();
            }
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("searchValue", searchValue);
            request.getRequestDispatcher("/warehouse-management/category-management.jsp").forward(request, response);
        } catch(Exception ex) {
            ex.printStackTrace();
            // Lỗi hệ thống -> quay lại list với msg=error (JSP sẽ hiển thị)
            response.sendRedirect(request.getContextPath() + "/warehouse-management/category-management?msg=error");
        }
    }
}
