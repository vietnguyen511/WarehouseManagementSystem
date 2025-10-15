package controller.supplierMgt;

import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

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
            boolean ok = dao.delete(id);
            response.sendRedirect(request.getContextPath() + "/suppliers?" + (ok ? "deleted=1" : "deleted=0"));
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/suppliers?error=1");
        }
    }
}


