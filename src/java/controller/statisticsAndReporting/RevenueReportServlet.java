package controller.statisticsAndReporting;

import dal.StatisticsDAO;
import dal.ProductDAO;
import dal.CategoryDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import model.RevenueStatDTO;
import model.Product;
import model.Category;

/**
 * RevenueReportServlet - Handles requests for revenue statistics and reporting
 * Displays charts and tables showing revenue data with filtering by product/category
 * 
 * @author lengo
 */
public class RevenueReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            System.out.println("=== RevenueReportServlet START ===");
            
            StatisticsDAO statsDAO = new StatisticsDAO();
            ProductDAO productDAO = new ProductDAO();
            CategoryDAO categoryDAO = new CategoryDAO();
            
            // Get filter parameters
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            String groupByParam = request.getParameter("groupBy");
            String productIdParam = request.getParameter("productId");
            String categoryIdParam = request.getParameter("categoryId");
            
            // Default grouping
            String groupBy = (groupByParam != null && !groupByParam.isEmpty()) ? groupByParam : "day";
            
            // Parse dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = null;
            Date endDate = null;
            
            try {
                if (startDateParam != null && !startDateParam.isEmpty()) {
                    startDate = sdf.parse(startDateParam);
                }
                if (endDateParam != null && !endDateParam.isEmpty()) {
                    endDate = sdf.parse(endDateParam);
                }
            } catch (ParseException e) {
                System.out.println("Error parsing dates: " + e.getMessage());
            }
            
            // Default to last 30 days if no dates provided
            if (endDate == null) {
                // Set end date to end of today to include all transactions today
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            } else {
                // If endDate is provided, set it to end of that day
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();
            }
            if (startDate == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
            } else {
                // If startDate is provided, set it to start of that day
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
            }
            
            // Parse filter IDs
            Integer productId = null;
            Integer categoryId = null;
            
            try {
                if (productIdParam != null && !productIdParam.isEmpty()) {
                    productId = Integer.parseInt(productIdParam);
                }
                if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                    categoryId = Integer.parseInt(categoryIdParam);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing filter IDs: " + e.getMessage());
            }
            
            System.out.println("Fetching revenue statistics from " + sdf.format(startDate) + " to " + sdf.format(endDate));
            System.out.println("Group by: " + groupBy);
            System.out.println("Product filter: " + productId);
            System.out.println("Category filter: " + categoryId);
            
            // Get revenue statistics
            List<RevenueStatDTO> statistics = statsDAO.getRevenueStatistics(startDate, endDate, groupBy, productId, categoryId);
            System.out.println("Retrieved " + statistics.size() + " revenue statistics records");
            
            // Pagination for table
            String pageParam = request.getParameter("page");
            int page = 1;
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            int pageSize = 10;
            int totalRecords = statistics.size();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // Ensure page is within bounds
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }
            
            // Get paginated list for table
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalRecords);
            List<RevenueStatDTO> paginatedStatistics = statistics.subList(fromIndex, toIndex);
            
            // Get summary statistics
            Object[] summary = statsDAO.getRevenueSummaryStatistics(startDate, endDate, productId, categoryId);
            System.out.println("Revenue summary statistics retrieved");
            
            // Get filter options
            List<Product> products = productDAO.getAllProducts();
            List<Category> categories = categoryDAO.getAllCategories();
            
            // Set attributes for JSP
            request.setAttribute("statistics", statistics); // Full list for charts
            request.setAttribute("paginatedStatistics", paginatedStatistics); // Paginated list for table
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("totalRevenue", summary[0]);
            request.setAttribute("totalQuantity", summary[1]);
            request.setAttribute("totalReceipts", summary[2]);
            request.setAttribute("averageOrderValue", summary[3]);
            request.setAttribute("topProductRevenue", summary[4]);
            
            // Set filter values
            request.setAttribute("startDate", sdf.format(startDate));
            request.setAttribute("endDate", sdf.format(endDate));
            request.setAttribute("groupBy", groupBy);
            request.setAttribute("selectedProductId", productId);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            
            // Forward to JSP
            System.out.println("Forwarding to JSP...");
            request.getRequestDispatcher("/statistics-and-reporting/revenue-report.jsp")
                   .forward(request, response);
            System.out.println("=== RevenueReportServlet END ===");
            
        } catch (Exception e) {
            System.out.println("Error in RevenueReportServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("errorMessage", "An error occurred while loading revenue report: " + e.getMessage());
            request.setAttribute("statistics", new java.util.ArrayList<RevenueStatDTO>());
            
            request.getRequestDispatcher("/statistics-and-reporting/revenue-report.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Revenue Report Servlet - Displays revenue statistics with charts and filtering options";
    }
}
