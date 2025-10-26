package controller.supplierMgt;

import dal.ActivityLogHelper;
import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import model.Supplier;

public class EditSupplierServlet extends HttpServlet {

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
            request.setAttribute("supplier", supplier);
            request.setAttribute("activePage", "suppliers");
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/supplier-mgt/supplier-form.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/suppliers?error=1");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String statusParam = request.getParameter("status");
            boolean status = "1".equals(statusParam) || "on".equalsIgnoreCase(statusParam) || "true".equalsIgnoreCase(statusParam);

            Supplier s = new Supplier();
            s.setSupplierId(id);
            s.setName(name);
            s.setPhone(phone);
            s.setEmail(email);
            s.setAddress(address);
            s.setStatus(status);

            SupplierDAO dao = new SupplierDAO();
            if (dao.update(s)) {
                // Log activity
                ActivityLogHelper.logUpdate(request.getSession(), "Suppliers", id, 
                    "Updated supplier: " + name);
                
                response.sendRedirect(request.getContextPath() + "/suppliers?updated=1");
                return;
            }
            request.setAttribute("error", "Failed to update supplier");
            request.setAttribute("supplier", s);
            request.setAttribute("activePage", "suppliers");
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/supplier-mgt/supplier-form.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error updating supplier: " + e.getMessage());
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/supplier-mgt/supplier-form.jsp").forward(request, response);
        }
    }
}


