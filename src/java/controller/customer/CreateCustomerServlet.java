package controller.customer;

import dal.ActivityLogHelper;
import dal.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.Customer;

public class CreateCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "customers");
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/customer-mgt/customer-form.jsp").forward(request, response);
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

        Customer s = new Customer();
        s.setName(name);
        s.setPhone(phone);
        s.setEmail(email);
        s.setAddress(address);
        s.setStatus(status);

        CustomerDAO dao = new CustomerDAO();
        try {
            int id = dao.create(s);
            if (id > 0) {
                // Log activity
                HttpSession session = request.getSession();
                ActivityLogHelper.logCreate(session, "Customers", id, 
                    "Created new customer: " + name);
                
                response.sendRedirect(request.getContextPath() + "/customers?created=1");
                return;
            }
            request.setAttribute("error", "Failed to create customer");
        } catch (SQLException e) {
            request.setAttribute("error", "Error creating customer: " + e.getMessage());
        }
        request.setAttribute("customer", s);
        request.setAttribute("activePage", "customers");
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/customer-mgt/customer-form.jsp").forward(request, response);
    }
}


