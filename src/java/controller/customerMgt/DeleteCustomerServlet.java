package controller.customerMgt;

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

public class DeleteCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
            String customerName = (customer != null) ? customer.getName() : "Unknown";
            
            boolean ok = dao.delete(id);
            
            if (ok) {
                
                HttpSession session = request.getSession();
                ActivityLogHelper.logDelete(session, "Customers", id, 
                    "Deleted customer: " + customerName);
            }
            
            response.sendRedirect(request.getContextPath() + "/customers?" + (ok ? "deleted=1" : "deleted=0"));
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/customers?error=1");
        }
    }
}


