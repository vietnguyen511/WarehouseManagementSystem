package controller.statisticsAndReporting;

import dal.StatisticsDAO;
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
import model.ImportExportStatDTO;

/**
 * ImportExportStatsServlet - Handles requests for import/export statistics
 * Displays charts and tables showing warehouse activity over time
 * 
 * @author lengo
 */
public class ImportExportStatsServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            System.out.println("=== ImportExportStatsServlet START ===");
            
            StatisticsDAO statsDAO = new StatisticsDAO();
            
            // Get filter parameters
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            String groupByParam = request.getParameter("groupBy");
            
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
            
            System.out.println("Fetching statistics from " + sdf.format(startDate) + " to " + sdf.format(endDate));
            System.out.println("Group by: " + groupBy);
            
            // Get statistics
            List<ImportExportStatDTO> statistics = statsDAO.getImportExportStatistics(startDate, endDate, groupBy);
            System.out.println("Retrieved " + statistics.size() + " statistics records");
            
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
            List<ImportExportStatDTO> paginatedStatistics = statistics.subList(fromIndex, toIndex);
            
            // Get summary statistics
            Object[] summary = statsDAO.getSummaryStatistics();
            System.out.println("Summary statistics retrieved");
            
            // Set attributes for JSP
            request.setAttribute("statistics", statistics); // Full list for charts
            request.setAttribute("paginatedStatistics", paginatedStatistics); // Paginated list for table
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("totalImports", summary[0]);
            request.setAttribute("totalExports", summary[1]);
            request.setAttribute("totalImportValue", summary[2]);
            request.setAttribute("totalExportValue", summary[3]);
            request.setAttribute("netProfit", summary[4]);
            
            // Set filter values
            request.setAttribute("startDate", sdf.format(startDate));
            request.setAttribute("endDate", sdf.format(endDate));
            request.setAttribute("groupBy", groupBy);
            
            // Forward to JSP
            System.out.println("Forwarding to JSP...");
            request.getRequestDispatcher("/statistics-and-reporting/import-export-stats.jsp")
                   .forward(request, response);
            System.out.println("=== ImportExportStatsServlet END ===");
            
        } catch (Exception e) {
            System.out.println("Error in ImportExportStatsServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("errorMessage", "An error occurred while loading statistics: " + e.getMessage());
            request.setAttribute("statistics", new java.util.ArrayList<ImportExportStatDTO>());
            
            request.getRequestDispatcher("/statistics-and-reporting/import-export-stats.jsp")
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
        return "Import/Export Statistics Servlet - Displays warehouse activity charts and reports";
    }
}

