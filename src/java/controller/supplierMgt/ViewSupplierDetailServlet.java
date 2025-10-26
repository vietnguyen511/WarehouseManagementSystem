package controller.supplierMgt;

import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import model.Supplier;

public class ViewSupplierDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/suppliers");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            SupplierDAO dao = new SupplierDAO();
            Supplier supplier = dao.findById(id);
            if (supplier == null) {
                response.sendRedirect(request.getContextPath() + "/suppliers?notfound=1");
                return;
            }
            
            // Get import statistics for this supplier
            Object[] stats = dao.getImportStatistics(id);
            request.setAttribute("totalReceipts", stats[0]);
            request.setAttribute("totalQuantity", stats[1]);
            request.setAttribute("totalAmount", stats[2]);
            
            request.setAttribute("supplier", supplier);
            request.setAttribute("activePage", "suppliers");
            request.getRequestDispatcher("/supplier-mgt/supplier-detail.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/suppliers?error=1");
        }
    }
}


