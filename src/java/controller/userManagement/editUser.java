package controller.userManagement;

import dal.UserDAO;
import model.User;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/edit-user")
public class editUser extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");

        //    User user = new User(id, fullname, email, phone, role);
      //      DAOUser dao = new DAOUser();
     //       dao.updateUser(user);

            response.sendRedirect("staff-list?success=edit");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staff-list?error=edit");
        }
    }
}
