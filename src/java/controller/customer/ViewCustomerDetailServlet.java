package controller.customer;

import dal.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import model.Customer;

public class ViewCustomerDetailServlet extends HttpServlet {

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
            
            
            Object[] stats = dao.getExportStatistics(id);
            request.setAttribute("totalReceipts", stats[0]);
            request.setAttribute("totalQuantity", stats[1]);
            request.setAttribute("totalAmount", stats[2]);
            
            request.setAttribute("customer", customer);
            request.setAttribute("activePage", "customers");
            request.getRequestDispatcher("/customer-mgt/customer-detail.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/customers?error=1");
        }
    }
}


