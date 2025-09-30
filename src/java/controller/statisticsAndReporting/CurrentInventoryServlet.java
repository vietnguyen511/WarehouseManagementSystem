/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.statisticsAndReporting;

import dal.CategoryDAO;
import dal.InventoryDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Category;
import model.InventoryItem;

/**
 * CurrentInventoryServlet - Handles requests for current inventory reporting
 * Displays product inventory with filtering and search capabilities
 * 
 * @author lengo
 */
public class CurrentInventoryServlet extends HttpServlet {

    private InventoryDAO inventoryDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize DAOs
        inventoryDAO = new InventoryDAO();
        categoryDAO = new CategoryDAO();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding for proper UTF-8 support
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // Get filter parameters from request
            String categoryIdParam = request.getParameter("categoryId");
            String searchQuery = request.getParameter("q");
            String pageParam = request.getParameter("page");
            String pageSizeParam = request.getParameter("pageSize");
            
            // Parse category ID
            Integer categoryId = null;
            if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
                try {
                    categoryId = Integer.parseInt(categoryIdParam);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid category ID: " + categoryIdParam);
                }
            }
            
            // Pagination defaults
            int page = 1;
            int pageSize = 10;
            try {
                if (pageParam != null && !pageParam.isEmpty()) {
                    page = Math.max(Integer.parseInt(pageParam), 1);
                }
                if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                    pageSize = Math.max(Integer.parseInt(pageSizeParam), 1);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid pagination params, using defaults.");
            }

            // Get total count and page data
            int totalItems = inventoryDAO.countCurrentInventory(categoryId, searchQuery);
            int totalPages = (int) Math.ceil(totalItems / (double) pageSize);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages;

            // Get inventory data with filters and pagination
            List<InventoryItem> inventoryList = inventoryDAO.getCurrentInventoryPaged(categoryId, searchQuery, page, pageSize);

            // Get all categories for the filter dropdown
            List<Category> categories = categoryDAO.getAllCategories();
            
            // Get inventory statistics (optional - for future dashboard use)
            Object[] statistics = inventoryDAO.getInventoryStatistics();
            
            // Set attributes for JSP
            request.setAttribute("inventoryList", inventoryList);
            request.setAttribute("categories", categories);
            request.setAttribute("totalProducts", statistics[0]);
            request.setAttribute("totalValue", statistics[1]);
            request.setAttribute("lowStockCount", statistics[2]);
            request.setAttribute("outOfStockCount", statistics[3]);
            
            // Set filter values for maintaining state
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("page", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("totalPages", totalPages);
            
            // Forward to JSP
            request.getRequestDispatcher("/statisticsAndReporting/current-inventory.jsp")
                   .forward(request, response);
            
        } catch (Exception e) {
            System.out.println("Error in CurrentInventoryServlet: " + e.getMessage());
            e.printStackTrace();
            
            // Set error message and forward to error page or show empty list
            request.setAttribute("errorMessage", "An error occurred while loading inventory data: " + e.getMessage());
            request.setAttribute("inventoryList", new java.util.ArrayList<InventoryItem>());
            request.setAttribute("categories", new java.util.ArrayList<Category>());
            
            request.getRequestDispatcher("/statisticsAndReporting/current-inventory.jsp")
                   .forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Current Inventory Servlet - Displays current warehouse inventory with filtering";
    }
}

