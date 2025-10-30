/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.warehouseManagement;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import model.Product;
import dal.ProductDAO;

/**
 *
 * @author DANG
 */
public class ProductManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
        ProductDAO dao = new ProductDAO();
        List<Product> products = new ArrayList<>();

        try {
            // check if search value is null or its trimmed is empty
            if (searchValue == null || searchValue.trim().isEmpty()) {               
                products = dao.getAllProducts();
            } else {
                searchValue = searchValue.trim();
                try {
                    int id = Integer.parseInt(searchValue);
                    Product product = dao.getProductById(id);
                    // check if product is not null
                    if (product != null) {
                        products.add(product);
                    }
                } catch (NumberFormatException e) {
                    products = dao.getProductByName(searchValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error orrured when retrieving data: " + e.getMessage());
        }

        request.setAttribute("products", products);
        request.setAttribute("searchValue", searchValue);
        request.getRequestDispatcher("/warehouse-management/product-management.jsp").forward(request, response);
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
        return "Short description";
    }// </editor-fold>

}
