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
 * @author admin
 */
public class CurrentInventoryServlet extends HttpServlet {

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
            System.out.println("=== CurrentInventoryServlet START ===");
            
            // Create fresh DAO instances for each request to avoid connection timeout issues
            InventoryDAO inventoryDAO = new InventoryDAO();
            CategoryDAO categoryDAO = new CategoryDAO();
            System.out.println("DAOs created successfully");
            
            // Get filter parameters from request
            String categoryIdParam = request.getParameter("categoryId");
            String searchQuery = request.getParameter("q");
            String pageParam = request.getParameter("page");
            String pageSizeParam = request.getParameter("pageSize");
            System.out.println("Parameters: categoryId=" + categoryIdParam + ", q=" + searchQuery + ", page=" + pageParam);
            
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
            int pageSize = 5;
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
            System.out.println("Calling countCurrentInventory...");
            int totalItems = inventoryDAO.countCurrentInventory(categoryId, searchQuery);
            System.out.println("Total items: " + totalItems);
            
            int totalPages = (int) Math.ceil(totalItems / (double) pageSize);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages;

            // Get inventory data with filters - use simple query without pagination for now
            System.out.println("Calling getCurrentInventory (without pagination)...");
            List<InventoryItem> allItems = inventoryDAO.getCurrentInventory(categoryId, searchQuery);
            System.out.println("Retrieved " + allItems.size() + " total items");
            
            // Manual pagination in Java (temporary workaround)
            int startIdx = Math.max(0, (page - 1) * pageSize);
            int endIdx = Math.min(allItems.size(), startIdx + pageSize);
            List<InventoryItem> inventoryList = allItems.subList(startIdx, endIdx);
            System.out.println("Showing items " + startIdx + " to " + endIdx);

            // Get all categories for the filter dropdown
            System.out.println("Calling getAllCategories...");
            List<Category> categories = categoryDAO.getAllCategories();
            System.out.println("Retrieved " + categories.size() + " categories");
            
            // Get inventory statistics (optional - for future dashboard use)
            System.out.println("Calling getInventoryStatistics...");
            Object[] statistics = inventoryDAO.getInventoryStatistics();
            System.out.println("Statistics retrieved");
            
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
            System.out.println("Forwarding to JSP...");
            request.getRequestDispatcher("/statistics-and-reporting/current-inventory.jsp")
                   .forward(request, response);
            System.out.println("=== CurrentInventoryServlet END ===");
            
        } catch (Exception e) {
            System.out.println("Error in CurrentInventoryServlet: " + e.getMessage());
            e.printStackTrace();
            
            // Set error message and forward to error page or show empty list
            request.setAttribute("errorMessage", "An error occurred while loading inventory data: " + e.getMessage());
            request.setAttribute("inventoryList", new java.util.ArrayList<InventoryItem>());
            request.setAttribute("categories", new java.util.ArrayList<Category>());
            
            request.getRequestDispatcher("/statistics-and-reporting/current-inventory.jsp")
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

