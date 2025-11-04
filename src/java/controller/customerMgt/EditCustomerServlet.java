package controller.customerMgt;

import dal.ActivityLogHelper;
import dal.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import model.Customer;

public class EditCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/customers");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            CustomerDAO dao = new CustomerDAO();
            Customer customer = dao.findById(id);
            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/customers?notfound=1");
                return;
            }
            request.setAttribute("customer", customer);
            request.setAttribute("activePage", "customers");
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/customer-mgt/customer-form.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/customers?error=1");
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

            Customer c = new Customer();
            c.setCustomerId(id);
            c.setName(name);
            c.setPhone(phone);
            c.setEmail(email);
            c.setAddress(address);
            c.setStatus(status);

            CustomerDAO dao = new CustomerDAO();
            if (dao.update(c)) {
                // Log activity
                ActivityLogHelper.logUpdate(request.getSession(), "Customers", id, 
                    "Updated customer: " + name);
                
                response.sendRedirect(request.getContextPath() + "/customers?updated=1");
                return;
            }
            request.setAttribute("error", "Failed to update customer");
            request.setAttribute("customer", c);
            request.setAttribute("activePage", "customers");
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/customer-mgt/customer-form.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error updating customer: " + e.getMessage());
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/customer-mgt/customer-form.jsp").forward(request, response);
        }
    }
}


