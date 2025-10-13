/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.warehouseManagement;

import dal.CategoryDAO;
import model.Category;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DANG
 */
@WebServlet("/category-management")
public class CategoryManagementServlet extends HttpServlet {

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
// for testing       
//        response.setContentType("text/plain");
//        PrintWriter out = response.getWriter();
        
        String searchValue = request.getParameter("searchValue");
//        out.println("Search value = " + searchValue);
        
        CategoryDAO dao = new CategoryDAO();
        List<Category> categoryList = new ArrayList<>();      
        
        // check if search value is not null and its trimmed value is not empty
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            try {               
                int categoryId = Integer.parseInt(searchValue.trim());
// for testing
//                out.println("Searching by ID: " + categoryId);
                Category category = dao.getCategoryById(categoryId);               
                // check if category is not null
                if (category != null) {
                    categoryList.add(category);
// for testing
//                    out.println("Found: " + category.getCategoryId() + " | " + category.getName());
                }
// for testing
//                else {
//                    out.println("No category found with ID = " + categoryId);
//                }
            } catch (NumberFormatException e) {
// for testing
//                out.println("Searching by name: " + searchValue.trim());
                categoryList = dao.getCategoryByName(searchValue.trim());
// for testing
//                // check if category list is empty
//                if (categoryList.isEmpty()) {
//                    out.println("No category found with name containing '" + searchValue + "'");
//                } else {
//                    // loop through all category list element
//                    for (Category category : categoryList) {
//                        out.println(category.getCategoryId() + " | " + category.getCode() + " | " + category.getName());
//                    }
//                }
//                out.println("Found " + categoryList.size() + " categories.");
            }
        } else {
// for testing
//            out.println("== ALL CATEGORIES ==");
            categoryList = dao.getAllCategories();
// for testing:
//            //loop through all category list element
//            for (Category category : categoryList) {
//                 out.println(category.getCategoryId() + " | " + category.getCode() + " | " + category.getName());
//            }
        }
        
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("searchValue", searchValue);
        request.getRequestDispatcher("/warehouse-management/category-management.jsp").forward(request, response);
    }
}
