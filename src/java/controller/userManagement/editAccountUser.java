package controller.userManagement;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/edit-account-user")
public class editAccountUser extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");        
          //   User user = new User(id, fullname, email, phone, role);
          //   UserDAO dao = new DAOUser();
          //   dao.updateUser(user);
            response.sendRedirect("staff-list?success=edit");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staff-list?error=edit");
        }
    }
}
