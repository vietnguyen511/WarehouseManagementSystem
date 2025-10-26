package controller;

import dal.ActivityLogDAO;
import model.ActivityLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ActivityLogServlet - Handles activity log viewing
 * 
 * @author lengo
 */
public class ActivityLogServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            System.out.println("=== ActivityLogServlet START ===");
            
            ActivityLogDAO dao = new ActivityLogDAO();
            
            // Get pagination parameters
            String pageParam = request.getParameter("page");
            int page = 1;
            int pageSize = 20;
            
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            // Get filter parameters
            String filterAction = request.getParameter("filterAction");
            String filterUser = request.getParameter("filterUser");
            
            // Get logs
            List<ActivityLog> logs;
            int totalRecords;
            
            if (filterAction != null && !filterAction.isEmpty()) {
                logs = dao.getActivityLogsByAction(filterAction, page, pageSize);
                totalRecords = dao.countActivityLogs(); // Simplified - should filter count too
            } else if (filterUser != null && !filterUser.isEmpty()) {
                // Search by username
                logs = dao.getActivityLogsByUsername(filterUser, page, pageSize);
                totalRecords = dao.countActivityLogsByUsername(filterUser);
            } else {
                logs = dao.getAllActivityLogs(page, pageSize);
                totalRecords = dao.countActivityLogs();
            }
            
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // Format dates
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (ActivityLog log : logs) {
                if (log.getCreatedAt() != null) {
                    log.setFormattedDate(sdf.format(log.getCreatedAt()));
                }
            }
            
            // Set attributes
            request.setAttribute("logs", logs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("filterAction", filterAction);
            request.setAttribute("filterUser", filterUser);
            
            // Forward to JSP
            System.out.println("Forwarding to JSP with " + logs.size() + " logs");
            request.getRequestDispatcher("/statistics-and-reporting/activity-log.jsp")
                   .forward(request, response);
            System.out.println("=== ActivityLogServlet END ===");
            
        } catch (Exception e) {
            System.out.println("Error in ActivityLogServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("errorMessage", "An error occurred while loading activity logs: " + e.getMessage());
            request.getRequestDispatcher("/statistics-and-reporting/activity-log.jsp")
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
}

