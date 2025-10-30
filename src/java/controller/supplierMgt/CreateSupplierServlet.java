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

public class CreateSupplierServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "suppliers");
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/supplier-mgt/supplier-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String statusParam = request.getParameter("status");
        boolean status = "1".equals(statusParam) || "on".equalsIgnoreCase(statusParam) || "true".equalsIgnoreCase(statusParam);

        Supplier s = new Supplier();
        s.setName(name);
        s.setPhone(phone);
        s.setEmail(email);
        s.setAddress(address);
        s.setStatus(status);

        SupplierDAO dao = new SupplierDAO();
        try {
            int id = dao.create(s);
            if (id > 0) {
                // Log activity
                HttpSession session = request.getSession();
                ActivityLogHelper.logCreate(session, "Suppliers", id, 
                    "Created new supplier: " + name);
                
                response.sendRedirect(request.getContextPath() + "/suppliers?created=1");
                return;
            }
            request.setAttribute("error", "Failed to create supplier");
        } catch (SQLException e) {
            request.setAttribute("error", "Error creating supplier: " + e.getMessage());
        }
        request.setAttribute("supplier", s);
        request.setAttribute("activePage", "add-supplier");
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/supplier-mgt/supplier-form.jsp").forward(request, response);
    }
}


