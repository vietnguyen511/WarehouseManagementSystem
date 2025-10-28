package controller.supplierMgt;

import dal.ActivityLogHelper;
import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.Supplier;

public class DeleteSupplierServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/suppliers");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            SupplierDAO dao = new SupplierDAO();
            
            // Get supplier info before deleting for logging
            Supplier supplier = dao.findById(id);
            String supplierName = (supplier != null) ? supplier.getName() : "Unknown";
            
            boolean ok = dao.delete(id);
            
            if (ok) {
                // Log activity
                HttpSession session = request.getSession();
                ActivityLogHelper.logDelete(session, "Suppliers", id, 
                    "Deleted supplier: " + supplierName);
            }
            
            response.sendRedirect(request.getContextPath() + "/suppliers?" + (ok ? "deleted=1" : "deleted=0"));
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/suppliers?error=1");
        }
    }
}


