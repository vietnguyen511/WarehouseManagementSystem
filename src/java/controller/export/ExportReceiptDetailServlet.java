package controller.export;

import dal.ExportDetailDAO;
import dal.ExportReceiptDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExportDetail;
import model.ExportReceipt;

@WebServlet(name = "ExportReceiptDetailServlet", urlPatterns = {"/warehouse-export-mgt/export-receipt-detail"})
public class ExportReceiptDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String exportIdParam = request.getParameter("exportId");
            
            if (exportIdParam == null || exportIdParam.trim().isEmpty()) {
                request.setAttribute("error", "Export receipt ID is required");
                request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-detail.jsp").forward(request, response);
                return;
            }
            
            int exportId;
            try {
                exportId = Integer.parseInt(exportIdParam);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid export receipt ID");
                request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-detail.jsp").forward(request, response);
                return;
            }
            
            ExportReceiptDAO exportReceiptDAO = new ExportReceiptDAO();
            ExportDetailDAO exportDetailDAO = new ExportDetailDAO();
            
            // Get export receipt information
            ExportReceipt exportReceipt = exportReceiptDAO.getExportReceiptById(exportId);
            
            if (exportReceipt == null) {
                request.setAttribute("error", "Export receipt not found");
                request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-detail.jsp").forward(request, response);
                return;
            }
            
            // Get export details
            List<ExportDetail> exportDetails = exportDetailDAO.getExportDetailsByExportId(exportId);
            
            request.setAttribute("exportReceipt", exportReceipt);
            request.setAttribute("exportDetails", exportDetails);
            request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-detail.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/warehouse-export-mgt/export-receipt-detail.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
