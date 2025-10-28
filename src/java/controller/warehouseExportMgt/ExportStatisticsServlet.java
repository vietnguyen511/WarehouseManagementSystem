package controller.warehouseExportMgt;

import dal.ExportReceiptDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import model.ExportStatDTO;

@WebServlet(name = "ExportStatisticsServlet", urlPatterns = {"/warehouse-export-mgt/export-statistics"})
public class ExportStatisticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get date range parameters
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String groupBy = request.getParameter("groupBy");
            
            // Default to last 30 days if no dates provided
            Date endDate;
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                endDate = parseDate(endDateStr);
            } else {
                endDate = new Date();
            }
            
            Date startDate;
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                startDate = parseDate(startDateStr);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startDate = cal.getTime();
            }
            
            // Default group by day
            if (groupBy == null || groupBy.trim().isEmpty()) {
                groupBy = "day";
            }
            
            // Pagination parameters
            int page = 1;
            int pageSize = 10;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            // Get statistics
            ExportReceiptDAO dao = new ExportReceiptDAO();
            List<ExportStatDTO> allStatistics = dao.getExportStatistics(startDate, endDate, groupBy);
            Object[] totalStats = dao.getTotalImportStatistics(startDate, endDate);
            
            // Pagination logic
            int totalRecords = allStatistics.size();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            if (page > totalPages && totalPages > 0) page = totalPages;
            
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalRecords);
            List<ExportStatDTO> statistics = allStatistics.subList(fromIndex, toIndex);
            
            // Set attributes
            request.setAttribute("statistics", statistics); // Paginated data for table
            request.setAttribute("allStatistics", allStatistics); // All data for chart
            request.setAttribute("totalReceipts", totalStats[0]);
            request.setAttribute("totalQuantity", totalStats[1]);
            request.setAttribute("totalAmount", totalStats[2]);
            request.setAttribute("avgAmount", totalStats[3]);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("groupBy", groupBy);
            
            // Pagination attributes
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("fromIndex", fromIndex);
            request.setAttribute("toIndex", Math.min(toIndex, totalRecords));
            
            // Format dates for display
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            request.setAttribute("startDateDisplay", sdf.format(startDate));
            request.setAttribute("endDateDisplay", sdf.format(endDate));
            
            request.getRequestDispatcher("/warehouse-import-mgt/import-statistics.jsp").forward(request, response);
            
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load export statistics: " + e.getMessage());
            request.getRequestDispatcher("/warehouse-export-mgt/export-statistics.jsp").forward(request, response);
        }
    }
    
    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateStr);
    }
}

