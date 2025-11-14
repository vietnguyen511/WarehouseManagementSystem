package controller.supplier;

import dal.ActivityLogHelper;
import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.Supplier;

@WebServlet(name = "ActivateSupplierServlet", urlPatterns = {"/suppliers/activate"})
public class ActivateSupplierServlet extends HttpServlet {

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
            
            // Get supplier info before activating for logging
            Supplier supplier = dao.findById(id);
            String supplierName = (supplier != null) ? supplier.getName() : "Unknown";
            
            // Activate supplier
            boolean ok = dao.activate(id);
            
            if (ok) {
                // Log activity
                HttpSession session = request.getSession();
                ActivityLogHelper.logUpdate(session, "Suppliers", id, 
                    "Activated supplier: " + supplierName);
            }
            
            response.sendRedirect(request.getContextPath() + "/suppliers?" + (ok ? "activated=1" : "activated=0"));
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/suppliers?error=1");
        }
    }
}

