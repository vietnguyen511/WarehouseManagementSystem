package controller.warehouseImportMgt;

import dal.ImportDetailDAO;
import dal.ImportReceiptDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ImportDetail;
import model.ImportReceipt;

@WebServlet(name = "ImportReceiptDetailServlet", urlPatterns = {"/warehouse-import-mgt/import-receipt-detail"})
public class ImportReceiptDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String importIdParam = request.getParameter("importId");
            
            if (importIdParam == null || importIdParam.trim().isEmpty()) {
                request.setAttribute("error", "Import receipt ID is required");
                request.getRequestDispatcher("/warehouse-import-mgt/import-receipt-detail.jsp").forward(request, response);
                return;
            }
            
            int importId;
            try {
                importId = Integer.parseInt(importIdParam);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid import receipt ID");
                request.getRequestDispatcher("/warehouse-import-mgt/import-receipt-detail.jsp").forward(request, response);
                return;
            }
            
            ImportReceiptDAO importReceiptDAO = new ImportReceiptDAO();
            ImportDetailDAO importDetailDAO = new ImportDetailDAO();
            
            // Get import receipt information
            ImportReceipt importReceipt = importReceiptDAO.getImportReceiptById(importId);
            
            if (importReceipt == null) {
                request.setAttribute("error", "Import receipt not found");
                request.getRequestDispatcher("/warehouse-import-mgt/import-receipt-detail.jsp").forward(request, response);
                return;
            }
            
            // Get import details
            List<ImportDetail> importDetails = importDetailDAO.getImportDetailsByImportId(importId);
            
            request.setAttribute("importReceipt", importReceipt);
            request.setAttribute("importDetails", importDetails);
            request.getRequestDispatcher("/warehouse-import-mgt/import-receipt-detail.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/warehouse-import-mgt/import-receipt-detail.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
