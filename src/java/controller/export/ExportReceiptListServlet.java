package controller.export;

import dal.ExportReceiptDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExportReceiptListDTO;

@WebServlet(name = "ExportReceiptListServlet", urlPatterns = {"/warehouse-export-mgt/export-receipt-list"})
public class ExportReceiptListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ExportReceiptDAO exportReceiptDAO = new ExportReceiptDAO();
            
            // Get search and filter parameters
            String searchTerm = request.getParameter("search");
            String dateFilter = request.getParameter("dateFilter");
            
            // Pagination parameters
            int page = 1;
            int recordsPerPage = 10;
            
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            String recordsPerPageParam = request.getParameter("recordsPerPage");
            if (recordsPerPageParam != null && !recordsPerPageParam.trim().isEmpty()) {
                try {
                    recordsPerPage = Integer.parseInt(recordsPerPageParam);
                    if (recordsPerPage < 1) recordsPerPage = 10;
                } catch (NumberFormatException e) {
                    recordsPerPage = 10;
                }
            }
            
            // Calculate offset
            int offset = (page - 1) * recordsPerPage;
            
            // Get total count and paginated data with search and filter
            int totalRecords = exportReceiptDAO.getTotalExportReceiptsCount(searchTerm, dateFilter);
            List<ExportReceiptListDTO> exportReceipts = exportReceiptDAO.getAllExportReceipts(offset, recordsPerPage, searchTerm, dateFilter);
            
            // Calculate pagination info
            int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
            int startRecord = totalRecords > 0 ? offset + 1 : 0;
            int endRecord = Math.min(offset + recordsPerPage, totalRecords);
            
            // Set attributes
            request.setAttribute("exportReceipts", exportReceipts);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("recordsPerPage", recordsPerPage);
            request.setAttribute("startRecord", startRecord);
            request.setAttribute("endRecord", endRecord);
            
            // Set search and filter attributes for form persistence
            request.setAttribute("searchTerm", searchTerm != null ? searchTerm : "");
            request.setAttribute("dateFilter", dateFilter != null ? dateFilter : "");

            // Set active page for sidebar navigation
            request.setAttribute("activePage", "export-receipt-list");

            request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-list.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tải danh sách phiếu nhập: " + e.getMessage());
            request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
